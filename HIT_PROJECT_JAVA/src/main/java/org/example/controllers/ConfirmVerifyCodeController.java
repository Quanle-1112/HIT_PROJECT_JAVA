package org.example.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ConfirmVerifyCodeController {

    @FXML private Button returnButton, verifyButton, resendCodeButton;
    @FXML private Button sendingCodeButton;
    @FXML private TextField codeTextField;
    @FXML private Label errorLabel;
    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Label expiredCodeText;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail, serverOtp;
    private long otpGeneratedTime;
    private static final long OTP_TIMEOUT = 5 * 60 * 1000;

    public void setInitData(String email, String otp) {
        this.userEmail = email;
        this.serverOtp = otp;
        this.otpGeneratedTime = System.currentTimeMillis();
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, expiredCodeText);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        verifyButton.setOnAction(event -> handleVerify());
        resendCodeButton.setOnAction(event -> handleResend());
        returnButton.setOnAction(event -> SceneUtils.switchScene(returnButton, "/view/forgot_password.fxml", "Forgot Password"));

        startTimer();
    }

    private void handleVerify() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, expiredCodeText);

        if (ValidationUtils.areFieldsEmpty(codeTextField)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        if (System.currentTimeMillis() - otpGeneratedTime > OTP_TIMEOUT) {
            UIExceptionHandler.showError(expiredCodeText);
            return;
        }

        if (codeTextField.getText().trim().equals(serverOtp)) {
            ChangePasswordToLoginController controller = SceneUtils.switchScene(verifyButton, "/view/change_password_to_login.fxml", "Đổi mật khẩu");
            if (controller != null) controller.setUserEmail(userEmail);
        } else {
            UIExceptionHandler.showError(errorLabel);
        }
    }

    private void handleResend() {
        resendCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            String newOtp = service.sendOtp(userEmail);
            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                resendCodeButton.setVisible(true);

                if (newOtp != null) {
                    this.serverOtp = newOtp;
                    this.otpGeneratedTime = System.currentTimeMillis();
                    startTimer();
                }
            });
        }).start();
    }

    private void startTimer() {
        resendCodeButton.setDisable(true);
        final int[] seconds = {30};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds[0]--;
            resendCodeButton.setText("Wait (" + seconds[0] + "s)");
            if (seconds[0] <= 0) {
                resendCodeButton.setDisable(false);
                resendCodeButton.setText("Resend code");
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();
    }
}