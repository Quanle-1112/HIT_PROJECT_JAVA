package org.example.controllers.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.constant.MessageConstant;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ChangePasswordToLoginController {
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Button updateButton, cancelButton;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    public void setUserEmail(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        updateButton.setOnAction(event -> handleUpdate());
        cancelButton.setOnAction(event -> SceneUtils.switchScene(cancelButton, "/view/authentication/forgot_password.fxml", "Quên mật khẩu"));
    }

    private void handleUpdate() {
        UIExceptionHandler.hideError(errorLabel);

        if (ValidationUtils.areFieldsEmpty(newPasswordField, confirmPasswordField)) {
            showError(MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }
        if (!ValidationUtils.isValidPassword(newPasswordField.getText())) {
            showError(MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showError(MessageConstant.REGISTER_PASSWORD_MISMATCH);
            return;
        }

        if (service.resetPassword(userEmail, newPasswordField.getText())) {
            SceneUtils.switchScene(updateButton, "/view/authentication/login.fxml", "Login");
        } else {
            showError(MessageConstant.UPDATE_FAIL);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        UIExceptionHandler.showError(errorLabel);
    }
}