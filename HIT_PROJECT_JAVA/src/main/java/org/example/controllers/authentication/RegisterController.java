package org.example.controllers.authentication;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
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
        closeButton.setOnAction(event ->
                SceneUtils.openNewWindow("/view/read/start_screen.fxml", "Start Screen", closeButton)
        );
    }

    private void handleRegister() {
        UIExceptionHandler.hideError(errorLabel);

        String email = emailTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String password = setPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationUtils.areFieldsEmpty(emailTextField, usernameTextField, setPasswordField, confirmPasswordField)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_EMPTY_FIELDS);
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_EMAIL_INVALID);
            return;
        }

        if (username.length() < 5) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_USERNAME_SHORT);
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }

        registerButton.setDisable(true);
        registerButton.setText(MessageConstant.REGISTER_SUCCESS_LOADING);

        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setFullName(username);

                registerService.register(newUser, confirmPassword);
                return null;
            }
        };

        registerTask.setOnSucceeded(e -> {
            UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, MessageConstant.REGISTER_SUCCESS, MessageConstant.REGISTER_SUCCESS);
            SceneUtils.openNewWindow("/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN, registerButton);
        });

        registerTask.setOnFailed(e -> {
            registerButton.setDisable(false);
            registerButton.setText(MessageConstant.TITLE_REGISTER);

            Throwable ex = registerTask.getException();

            if (ex instanceof AuthException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else if (ex instanceof AppException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(registerTask).start();
    }
}