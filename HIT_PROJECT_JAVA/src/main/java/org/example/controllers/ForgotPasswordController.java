package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import java.io.IOException;

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
        hideAllErrors();
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        sendRecoveryCodeButton.setOnAction(event -> handleSendCode());
        backToLoginButton.setOnAction(event -> navigateToLogin());
    }

    private void handleSendCode() {
        hideAllErrors();
        String email = emailToResetPasswordText.getText().trim();

        if (email.isEmpty()) {
            pleaseCompleteAllFieldsText.setVisible(true);
            return;
        }

        sendRecoveryCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            String otp = forgotPasswordService.sendOtp(email);

            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                sendRecoveryCodeButton.setVisible(true);

                if (otp != null) {
                    navigateToConfirmCode(email, otp);
                } else {
                    emailNotFoundText.setVisible(true);
                }
            });
        }).start();
    }

    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToConfirmCode(String email, String otp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/confirm_verify_code.fxml"));
            Parent root = loader.load();
            ConfirmVerifyCodeController controller = loader.getController();
            controller.setInitData(email, otp);
            Stage stage = (Stage) sendRecoveryCodeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hideAllErrors() {
        emailNotFoundText.setVisible(false);
        pleaseCompleteAllFieldsText.setVisible(false);
    }
}