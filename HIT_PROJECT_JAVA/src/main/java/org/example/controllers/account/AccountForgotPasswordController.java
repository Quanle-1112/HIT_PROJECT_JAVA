package org.example.controllers.account;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.dao.UserDAO;
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

    private Timeline timeline;
    private int countdownTime = 30;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        btnClose.setOnAction(e -> closeDialog());

        if (currentUser != null) {
            lblStatus.setText("Đang gửi mã tới: " + currentUser.getEmail());
            sendOtp();
        } else {
            setStatusError("Lỗi: Không tìm thấy thông tin người dùng.");
            setAllDisable(true);
        }

        btnVerifyOtp.setOnAction(e -> handleVerifyOtp());
        btnResendOtp.setOnAction(e -> handleResendOtp());
        btnSavePassword.setOnAction(e -> handleSavePassword());
    }

    private void sendOtp() {
        btnResendOtp.setDisable(true);

        new Thread(() -> {
            boolean success = userService.sendOtp(currentUser.getEmail());

            Platform.runLater(() -> {
                if (success) {
                    lblStatus.setText("Mã OTP đã được gửi tới email của bạn.");
                    lblStatus.setStyle("-fx-text-fill: #19345D;");
                    startTimer();
                } else {
                    setStatusError("Gửi mã thất bại. Vui lòng kiểm tra mạng.");
                    btnResendOtp.setDisable(false);
                }
            });
        }).start();
    }

    private void handleResendOtp() {
        lblStatus.setText("Đang gửi lại mã...");
        lblStatus.setStyle("-fx-text-fill: #666;");
        sendOtp();
    }

    private void startTimer() {
        countdownTime = 30;
        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            countdownTime--;
            btnResendOtp.setText("Gửi lại mã (" + countdownTime + "s)");

            if (countdownTime <= 0) {
                timeline.stop();
                btnResendOtp.setText("Gửi lại mã");
                btnResendOtp.setDisable(false);
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();
    }

    private void handleVerifyOtp() {
        String otp = txtOtp.getText().trim();
        if (otp.isEmpty()) {
            setStatusError("Vui lòng nhập mã OTP!");
            return;
        }

        OtpStatus status = userService.verifyOtp(currentUser.getEmail(), otp);

        if (status == OtpStatus.SUCCESS) {
            lblStatus.setText("Xác thực thành công! Hãy nhập mật khẩu mới.");
            lblStatus.setStyle("-fx-text-fill: #19345D; -fx-font-weight: bold;");

            passwordContainer.setDisable(false);
            txtOtp.setDisable(true);
            btnVerifyOtp.setDisable(true);
            btnResendOtp.setDisable(true);
            if (timeline != null) timeline.stop();
            btnResendOtp.setText("Đã xác thực");

        } else if (status == OtpStatus.EXPIRED_CODE) {
            setStatusError("Mã OTP đã hết hạn. Vui lòng gửi lại.");
        } else {
            setStatusError("Mã OTP không chính xác.");
        }
    }

    private void handleSavePassword() {
        String newPass = txtNewPass.getText();
        String confirmPass = txtConfirmPass.getText();

        if (ValidationUtils.areFieldsEmpty(txtNewPass, txtConfirmPass)) {
            setStatusError("Vui lòng điền đầy đủ mật khẩu.");
            return;
        }
        if (!ValidationUtils.isValidPassword(newPass)) {
            setStatusError("Mật khẩu yếu! (Cần 6-20 ký tự, hoa, thường, số, ký tự đặc biệt)");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            setStatusError("Mật khẩu xác nhận không khớp.");
            return;
        }

        String hashedPassword = EncryptionUtils.hashPassword(newPass);
        boolean success = userDAO.updateUserPassword(currentUser.getId(), hashedPassword);

        if (success) {
            currentUser.setPassword(hashedPassword);
            SessionManager.getInstance().setCurrentUser(currentUser);

            lblStatus.setText("Đổi mật khẩu thành công!");
            lblStatus.setStyle("-fx-text-fill: #19345D; -fx-font-weight: bold; -fx-font-size: 14px;");

            btnSavePassword.setDisable(true);
            passwordContainer.setDisable(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> closeDialog());
            pause.play();

        } else {
            setStatusError("Lỗi hệ thống: Không thể lưu mật khẩu.");
        }
    }

    private void setStatusError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: red;");
    }

    private void closeDialog() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private void setAllDisable(boolean disable) {
        txtOtp.setDisable(disable);
        btnVerifyOtp.setDisable(disable);
        btnResendOtp.setDisable(disable);
        passwordContainer.setDisable(disable);
    }
}