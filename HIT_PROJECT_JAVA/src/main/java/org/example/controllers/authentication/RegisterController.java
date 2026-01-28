package org.example.controllers.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Label emailAlreadyRegisteredText;
    @FXML private Label passwordConfirmationMismatchText;
    @FXML private Label usernameAlreadyExistsTextLabel;
    @FXML private Label invalidEmailFormatText;
    @FXML private Label usernameMustBeAtLeast5CharactersLongText;
    @FXML private Label errorFormatPasswordText;

    private final IRegisterService registerService = new IRegisterServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText,
                emailAlreadyRegisteredText,
                passwordConfirmationMismatchText,
                usernameAlreadyExistsTextLabel,
                invalidEmailFormatText,
                usernameMustBeAtLeast5CharactersLongText,
                errorFormatPasswordText
        );

        registerButton.setOnAction(event -> handleRegister());
        closeButton.setOnAction(event -> SceneUtils.openNewWindow("/view/read/start_screen.fxml", "Welcome", closeButton));
    }

    private void handleRegister() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText, emailAlreadyRegisteredText, passwordConfirmationMismatchText,
                usernameAlreadyExistsTextLabel, invalidEmailFormatText, usernameMustBeAtLeast5CharactersLongText,
                errorFormatPasswordText
        );

        String email = emailTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String password = setPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationUtils.areFieldsEmpty(emailTextField, usernameTextField, setPasswordField, confirmPasswordField)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            UIExceptionHandler.showError(invalidEmailFormatText);
            return;
        }

        if (username.length() < 5) {
            UIExceptionHandler.showError(usernameMustBeAtLeast5CharactersLongText);
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            UIExceptionHandler.showError(errorFormatPasswordText);
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIExceptionHandler.showError(passwordConfirmationMismatchText);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFullName(username);

        String result = registerService.register(newUser, confirmPassword);

        switch (result) {
            case "SUCCESS":
                SceneUtils.openNewWindow("/view/authentication/login.fxml", "Login", registerButton);
                break;
            case "Email đã được đăng ký!":
                UIExceptionHandler.showError(emailAlreadyRegisteredText);
                break;
            case "Tên đăng nhập đã tồn tại!":
                UIExceptionHandler.showError(usernameAlreadyExistsTextLabel);
                break;
            default:
                pleaseCompleteAllFieldsText.setText(result);
                UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
                break;
        }
    }
}