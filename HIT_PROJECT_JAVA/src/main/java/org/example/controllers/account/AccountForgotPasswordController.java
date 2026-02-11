package org.example.controllers.account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.EncryptionUtils;
import org.example.utils.SessionManager;
import org.example.utils.ValidationUtils;

public class AccountForgotPasswordController {

    @FXML private Label lblStatus;
    @FXML private Button btnClose;

    @FXML private TextField txtOtp;
    @FXML private Button btnVerifyOtp;
    @FXML private Button btnResendOtp;

    @FXML private VBox passwordContainer;
    @FXML private PasswordField txtNewPass;
    @FXML private PasswordField txtConfirmPass;
    @FXML private Button btnSavePassword;

    private final IUserService userService = new IUserServiceImpl();
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        UIExceptionHandler.hideError(lblStatus);

        passwordContainer.setDisable(true);
        passwordContainer.setOpacity(0.5);
        btnSavePassword.setDisable(true);

        btnClose.setOnAction(e -> closeDialog());
        btnResendOtp.setOnAction(e -> handleSendOtp());
        btnVerifyOtp.setOnAction(e -> handleVerifyOtp());
        btnSavePassword.setOnAction(e -> handleSavePassword());

        handleSendOtp();
    }

    private void handleSendOtp() {
        UIExceptionHandler.hideError(lblStatus);
        btnResendOtp.setDisable(true);
        btnResendOtp.setText(MessageConstant.CONFIRM_LOADING);

        Task<Boolean> sendTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return userService.sendOtp(currentUser.getEmail());
            }
        };

        sendTask.setOnSucceeded(e -> {
            btnResendOtp.setDisable(false);
            btnResendOtp.setText(MessageConstant.RESEND + " mã");
            if (sendTask.getValue()) {
                lblStatus.setText(MessageConstant.OTP_SENT_SUCCESS);
                lblStatus.setStyle("-fx-text-fill:  #19345D;");
            } else {
                UIExceptionHandler.showError(lblStatus, MessageConstant.OTP_RESEND_FAIL);
            }
        });

        sendTask.setOnFailed(e -> {
            btnResendOtp.setDisable(false);
            btnResendOtp.setText(MessageConstant.RESEND + " mã");

            Throwable ex = sendTask.getException();
            if (ex instanceof AppException) {
                UIExceptionHandler.showError(lblStatus, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), lblStatus);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(sendTask).start();
    }

    private void handleVerifyOtp() {
        UIExceptionHandler.hideError(lblStatus);
        String otp = txtOtp.getText().trim();

        if (otp.isEmpty()) {
            UIExceptionHandler.showError(lblStatus, MessageConstant.OTP_EMPTY);
            return;
        }

        Task<Void> verifyTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                OtpStatus status = userService.verifyOtp(currentUser.getEmail(), otp);
                if (status != OtpStatus.SUCCESS) {
                    throw new AuthException(MessageConstant.OTP_INVALID);
                }
                return null;
            }
        };

        verifyTask.setOnSucceeded(e -> {
            lblStatus.setText("Xác thực thành công. Nhập mật khẩu mới.");
            lblStatus.setStyle("-fx-text-fill:  #19345D;");

            txtOtp.setDisable(true);
            btnVerifyOtp.setDisable(true);
            btnResendOtp.setDisable(true);

            passwordContainer.setDisable(false);
            passwordContainer.setOpacity(1.0);
            btnSavePassword.setDisable(false);
        });

        verifyTask.setOnFailed(e -> {
            Throwable ex = verifyTask.getException();
            UIExceptionHandler.showError(lblStatus, ex.getMessage());

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(verifyTask).start();
    }

    private void handleSavePassword() {
        String newPass = txtNewPass.getText();
        String confirmPass = txtConfirmPass.getText();

        if (!ValidationUtils.isValidPassword(newPass)) {
            UIExceptionHandler.showError(lblStatus, MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            UIExceptionHandler.showError(lblStatus, MessageConstant.REGISTER_PASSWORD_MISMATCH);
            return;
        }

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String hashed = EncryptionUtils.hashPassword(newPass);
                if (!userDAO.updateUserPassword(currentUser.getId(), hashed)) {
                    throw new AppException(MessageConstant.UPDATE_FAIL);
                }
                currentUser.setPassword(hashed);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, MessageConstant.UPDATE_SUCCESS, MessageConstant.CHANGE_PASS_SUCCESS);
            closeDialog();
        });

        saveTask.setOnFailed(e -> {
            Throwable ex = saveTask.getException();
            UIExceptionHandler.showError(lblStatus, ex.getMessage());

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(saveTask).start();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}