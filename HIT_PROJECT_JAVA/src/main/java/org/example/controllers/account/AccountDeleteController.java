package org.example.controllers.account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.SessionManager;

import java.io.IOException;

public class AccountDeleteController {

    @FXML private Label lblStatus;
    @FXML private TextField txtOtp;
    @FXML private Button btnResendOtp;
    @FXML private Button btnConfirmDelete;
    @FXML private Button btnCancel;
    @FXML private Button btnClose;

    private final IUserService userService = new IUserServiceImpl();
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        UIExceptionHandler.hideError(lblStatus);

        btnResendOtp.setOnAction(e -> handleSendOtp());
        btnConfirmDelete.setOnAction(e -> handleConfirmDelete());

        btnCancel.setOnAction(e -> closeDialog());
        btnClose.setOnAction(e -> closeDialog());

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
                lblStatus.setStyle(MessageConstant.BACKGROUND_COLOR);
            } else {
                UIExceptionHandler.showError(lblStatus, MessageConstant.OTP_RESEND_FAIL);
            }
        });

        sendTask.setOnFailed(e -> {
            btnResendOtp.setDisable(false);
            btnResendOtp.setText(MessageConstant.RESEND + " mã");

            Throwable ex = sendTask.getException();
            UIExceptionHandler.handle(new Exception(ex), lblStatus);

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(sendTask).start();
    }

    private void handleConfirmDelete() {
        String otp = txtOtp.getText().trim();
        if (otp.isEmpty()) {
            UIExceptionHandler.showError(lblStatus, MessageConstant.OTP_EMPTY);
            return;
        }

        btnConfirmDelete.setDisable(true);
        btnConfirmDelete.setText(MessageConstant.CONFIRM_LOADING);

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                OtpStatus status = userService.verifyOtp(currentUser.getEmail(), otp);
                if (status != OtpStatus.SUCCESS) {
                    throw new AuthException(MessageConstant.OTP_INVALID);
                }

                if (!userDAO.deleteUser(currentUser.getId())) {
                    throw new AppException(MessageConstant.ERR_DB_DELETE);
                }
                return null;
            }
        };

        deleteTask.setOnSucceeded(e -> {
            performLogoutAndRedirect();
        });

        deleteTask.setOnFailed(e -> {
            btnConfirmDelete.setDisable(false);
            btnConfirmDelete.setText(MessageConstant.CONFIRM_DELETE);

            Throwable ex = deleteTask.getException();
            UIExceptionHandler.showError(lblStatus, ex.getMessage());

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(deleteTask).start();
    }

    private void performLogoutAndRedirect() {
        SessionManager.getInstance().logout();

        Stage dialogStage = (Stage) btnConfirmDelete.getScene().getWindow();
        Window owner = dialogStage.getOwner();
        dialogStage.close();

        if (owner instanceof Stage) {
            Stage mainStage = (Stage) owner;
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/read/start_screen.fxml"));
                mainStage.setScene(new Scene(root));
                mainStage.setTitle(MessageConstant.TITLE_APP);
                mainStage.centerOnScreen();
            } catch (IOException e) {
                throw new AppException(MessageConstant.ERR_SYSTEM, e);
            }
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}