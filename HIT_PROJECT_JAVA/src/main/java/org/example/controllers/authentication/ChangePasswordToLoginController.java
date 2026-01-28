package org.example.controllers.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ChangePasswordToLoginController {
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private Label errorLabel, pleaseCompleteAllFieldsText, errorFormatPasswordText;
    @FXML private Button updateButton, cancelButton;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    public void setUserEmail(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, errorFormatPasswordText);
        updateButton.setOnAction(event -> handleUpdate());
        cancelButton.setOnAction(event -> SceneUtils.switchScene(cancelButton, "/view/authentication/forgot_password.fxml", "Forgot Password"));
    }

    private void handleUpdate() {
        UIExceptionHandler.hideError(errorLabel, pleaseCompleteAllFieldsText, errorFormatPasswordText);

        if (ValidationUtils.areFieldsEmpty(newPasswordField, confirmPasswordField)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText); return;
        }
        if (!ValidationUtils.isValidPassword(newPasswordField.getText())) {
            UIExceptionHandler.showError(errorFormatPasswordText); return;
        }
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            UIExceptionHandler.showError(errorLabel); return;
        }

        if (service.resetPassword(userEmail, newPasswordField.getText())) {
            SceneUtils.switchScene(updateButton, "/view/authentication/login.fxml", "Login");
        }
    }
}