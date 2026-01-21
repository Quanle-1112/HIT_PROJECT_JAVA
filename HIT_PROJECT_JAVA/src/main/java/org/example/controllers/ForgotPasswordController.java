package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.OtpStatus;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ForgotPasswordController {
    @FXML private TextField emailToResetPasswordText;
    @FXML private Label emailNotFoundText, pleaseCompleteAllFieldsText;
    @FXML private Button sendRecoveryCodeButton, sendingCodeButton, backToLoginButton;

    private final IForgotPasswordService forgotPasswordService = new IForgotPasswordServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(emailNotFoundText, pleaseCompleteAllFieldsText);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        sendRecoveryCodeButton.setOnAction(event -> handleSendCode());
        backToLoginButton.setOnAction(event -> SceneUtils.switchScene(backToLoginButton, "/view/login.fxml", "Login"));
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(emailNotFoundText, pleaseCompleteAllFieldsText);
        String email = emailToResetPasswordText.getText().trim();

        if (ValidationUtils.areFieldsEmpty(emailToResetPasswordText)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText); return;
        }

        sendRecoveryCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            OtpStatus status = forgotPasswordService.sendOtp(email);

            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                sendRecoveryCodeButton.setVisible(true);

                if (status == OtpStatus.SUCCESS) {
                    ConfirmVerifyCodeController controller = SceneUtils.switchScene(sendRecoveryCodeButton, "/view/confirm_verify_code.fxml", "Xác nhận OTP");
                    if (controller != null) controller.setInitData(email);
                } else if (status == OtpStatus.EMAIL_NOT_EXIST) {
                    UIExceptionHandler.showError(emailNotFoundText);
                } else {
                    System.err.println("Gửi mail thất bại: " + status);
                }
            });
        }).start();
    }
}