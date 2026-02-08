package org.example.controllers.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.constant.MessageConstant;
import org.example.services.IRegisterService;
import org.example.services.impl.IRegisterServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class RegisterController {

    @FXML private TextField emailTextField;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField setPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button closeButton;

    @FXML private Label errorLabel;

    private final IRegisterService registerService = new IRegisterServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);

        registerButton.setOnAction(event -> handleRegister());
        closeButton.setOnAction(event -> SceneUtils.openNewWindow("/view/read/start_screen.fxml", "Start Screen", closeButton));
    }

    private void handleRegister() {
        UIExceptionHandler.hideError(errorLabel);

        String email = emailTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String password = setPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationUtils.areFieldsEmpty(emailTextField, usernameTextField, setPasswordField, confirmPasswordField)) {
            showError(MessageConstant.REGISTER_EMPTY_FIELDS);
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showError(MessageConstant.REGISTER_EMAIL_INVALID);
            return;
        }

        if (username.length() < 5) {
            showError(MessageConstant.REGISTER_USERNAME_SHORT);
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showError(MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(MessageConstant.REGISTER_PASSWORD_MISMATCH);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFullName(username);

        String result = registerService.register(newUser, confirmPassword);

        if (MessageConstant.REGISTER_SUCCESS.equals(result)) {
            SceneUtils.openNewWindow("/view/authentication/login.fxml", "Đăng nhập", registerButton);
        } else {
            showError(result);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        UIExceptionHandler.showError(errorLabel);
    }
}