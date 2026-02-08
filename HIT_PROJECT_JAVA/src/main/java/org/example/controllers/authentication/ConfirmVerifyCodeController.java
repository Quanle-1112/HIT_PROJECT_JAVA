package org.example.controllers.authentication;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.OtpStatus;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;

public class ConfirmVerifyCodeController {
    @FXML private Button returnButton, verifyButton, resendCodeButton, sendingCodeButton;
    @FXML private TextField codeTextField;
    @FXML private Label errorLabel;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    public void setInitData(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        verifyButton.setOnAction(event -> handleVerify());
        resendCodeButton.setOnAction(event -> handleResend());
        returnButton.setOnAction(event -> SceneUtils.switchScene(returnButton, "/view/authentication/forgot_password.fxml", "Quên mật khẩu"));
    }

    private void handleVerify() {
        UIExceptionHandler.hideError(errorLabel);
        String otp = codeTextField.getText().trim();

        if (otp.isEmpty()) {
            showError("Vui lòng nhập mã OTP!");
            return;
        }

        OtpStatus status = service.verifyOtp(userEmail, otp);

        if (status == OtpStatus.SUCCESS) {
            ChangePasswordToLoginController controller = SceneUtils.switchScene(verifyButton, "/view/authentication/change_password_to_login.fxml", "Đổi mật khẩu");
            if (controller != null) controller.setUserEmail(userEmail);
        } else if (status == OtpStatus.EXPIRED_CODE) {
            showError("Mã OTP đã hết hạn. Vui lòng gửi lại!");
        } else {
            showError("Mã OTP không chính xác!");
        }
    }

    private void handleResend() {
        resendCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            OtpStatus status = service.sendOtp(userEmail);
            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                resendCodeButton.setVisible(true);

                if (status == OtpStatus.SUCCESS) {
                    showError("Đã gửi lại mã OTP vào email!");
                    errorLabel.setStyle("-fx-text-fill: green;");
                    startTimer();
                } else {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    showError("Gửi lại thất bại. Vui lòng thử lại sau.");
                }
            });
        }).start();
    }

    private void startTimer() {
        resendCodeButton.setDisable(true);
        final int[] seconds = {30};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds[0]--;
            resendCodeButton.setText("Chờ (" + seconds[0] + "s)");
            if (seconds[0] <= 0) {
                resendCodeButton.setDisable(false);
                resendCodeButton.setText("Gửi lại mã");
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();
    }

    private void showError(String message) {
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setText(message);
        UIExceptionHandler.showError(errorLabel);
    }
}