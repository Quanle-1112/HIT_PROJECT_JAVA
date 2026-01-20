package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ForgotPasswordController {
    @FXML private TextField emailToResetPasswordText;
    @FXML private Label emailNotFoundText;
    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Button sendRecoveryCodeButton;
    @FXML private Button sendingCodeButton;
    @FXML private Button backToLoginButton;

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

        if (ValidationUtils.areFieldsEmpty(emailToResetPasswordText)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        String email = emailToResetPasswordText.getText().trim();
        sendRecoveryCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            String otp = forgotPasswordService.sendOtp(email);
            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                sendRecoveryCodeButton.setVisible(true);

                if (otp != null) {
                    ConfirmVerifyCodeController controller = SceneUtils.switchScene(sendRecoveryCodeButton, "/view/confirm_verify_code.fxml", "Xác nhận OTP");
                    if (controller != null) controller.setInitData(email, otp);
                } else {
                    UIExceptionHandler.showError(emailNotFoundText);
                }
            });
        }).start();
    }
}