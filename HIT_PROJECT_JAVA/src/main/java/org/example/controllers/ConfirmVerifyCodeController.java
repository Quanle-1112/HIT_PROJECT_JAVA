package org.example.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.exception.ErrorType;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import java.io.IOException;

public class ConfirmVerifyCodeController {

    @FXML private Button returnButton, verifyButton, resendCodeButton;
    @FXML private Button sendingCodeButton;
    @FXML private TextField codeTextField;

    @FXML private Label errorLabel;
    @FXML private Label pleaseCompleteAllFieldsText;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail, serverOtp;

    public void setInitData(String email, String otp) { this.userEmail = email; this.serverOtp = otp; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText);

        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        verifyButton.setOnAction(event -> handleVerify());
        resendCodeButton.setOnAction(event -> handleResend());
        returnButton.setOnAction(event -> navigateBack());
    }

    private void handleVerify() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText);

        String inputCode = codeTextField.getText().trim();
        if (inputCode.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText, ErrorType.PLEASE_COMPLETE_ALL_FIELDS);
            return;
        }
        if (inputCode.equals(serverOtp)) {
            navigateToChangePassword();
        } else {
            UIExceptionHandler.showError(errorLabel, ErrorType.INVALID_CODE);
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

    private void navigateToChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/change_password_to_login.fxml"));
            Parent root = loader.load();
            ChangePasswordToLoginController controller = loader.getController();
            controller.setUserEmail(userEmail);
            Stage stage = (Stage) verifyButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void navigateBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/forgot_password.fxml"));
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}