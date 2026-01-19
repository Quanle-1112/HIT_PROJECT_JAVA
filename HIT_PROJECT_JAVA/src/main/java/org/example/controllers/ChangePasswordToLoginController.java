package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import java.io.IOException;
import java.util.regex.Pattern;

public class ChangePasswordToLoginController {
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Label errorFormatPasswordText;
    @FXML private Button updateButton, cancelButton;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,10}$";

    public void setUserEmail(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, errorFormatPasswordText);
        updateButton.setOnAction(event -> handleUpdate());
        cancelButton.setOnAction(event -> navigateTo("/view/forgot_password.fxml"));
    }

    private void handleUpdate() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, errorFormatPasswordText);

        String pass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (pass.isEmpty() || confirm.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        if (!Pattern.matches(PASSWORD_PATTERN, pass)) {
            UIExceptionHandler.showError(errorFormatPasswordText);
            return;
        }

        if (!pass.equals(confirm)) {
            UIExceptionHandler.showError(errorLabel);
            return;
        }

        if (service.resetPassword(userEmail, pass)) {
            navigateTo("/view/login.fxml");
        }
    }

    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}