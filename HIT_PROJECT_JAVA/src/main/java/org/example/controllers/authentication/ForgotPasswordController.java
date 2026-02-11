package org.example.controllers.authentication;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.NetworkException;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ForgotPasswordController {
    @FXML private TextField emailToResetPasswordText;
    @FXML private Label errorLabel;
    @FXML private Button sendRecoveryCodeButton, sendingCodeButton, backToLoginButton;

    private final IForgotPasswordService forgotPasswordService = new IForgotPasswordServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        sendRecoveryCodeButton.setOnAction(event -> handleSendCode());
        backToLoginButton.setOnAction(event ->
                SceneUtils.switchScene(backToLoginButton, "/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN)
        );
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(errorLabel);
        String email = emailToResetPasswordText.getText().trim();

        if (ValidationUtils.areFieldsEmpty(emailToResetPasswordText)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.FORGOT_PASS_EMAIL_EMPTY);
            return;
        }

        sendRecoveryCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        Task<Void> sendOtpTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                forgotPasswordService.sendOtp(email);
                return null;
            }
        };

        sendOtpTask.setOnSucceeded(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            sendRecoveryCodeButton.setVisible(true);

            ConfirmVerifyCodeController controller = SceneUtils.switchScene(
                    sendRecoveryCodeButton,
                    "/view/authentication/confirm_verify_code.fxml",
                    "Xác nhận OTP"
            );
            if (controller != null) controller.setInitData(email);
        });

        sendOtpTask.setOnFailed(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            sendRecoveryCodeButton.setVisible(true);

            Throwable ex = sendOtpTask.getException();
            if (ex instanceof AuthException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else if (ex instanceof NetworkException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }
        });

        new Thread(sendOtpTask).start();
    }
}