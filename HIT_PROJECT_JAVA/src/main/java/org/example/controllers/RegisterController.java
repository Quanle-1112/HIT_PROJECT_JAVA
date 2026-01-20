package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.ErrorType;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.services.IRegisterService;
import org.example.services.impl.IRegisterServiceImpl;

import java.io.IOException;

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

    private final IRegisterService registerService = new IRegisterServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText,
                emailAlreadyRegisteredText,
                passwordConfirmationMismatchText,
                usernameAlreadyExistsTextLabel
        );

        registerButton.setOnAction(event -> handleRegister());
        closeButton.setOnAction(event -> handleClose());
    }

    private void handleRegister() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText,
                emailAlreadyRegisteredText,
                passwordConfirmationMismatchText,
                usernameAlreadyExistsTextLabel
        );

        String email = emailTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String pass = setPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText, ErrorType.PLEASE_COMPLETE_ALL_FIELDS);
            return;
        }

        if (!pass.equals(confirmPass)) {
            UIExceptionHandler.showError(passwordConfirmationMismatchText, ErrorType.PASSWORD_MISMATCH);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(pass);
        newUser.setFullName(username);

        String result = registerService.register(newUser, confirmPass);

        switch (result) {
            case "SUCCESS":
                openScreen("/view/login.fxml", "Login");
                break;

            case "Email đã được đăng ký!":
                UIExceptionHandler.showError(emailAlreadyRegisteredText, ErrorType.EMAIL_ALREADY_REGISTERED);
                break;

            case "Tên đăng nhập đã tồn tại!":
                UIExceptionHandler.showError(usernameAlreadyExistsTextLabel, ErrorType.USERNAME_ALREADY_EXISTS);
                break;

            default:
                UIExceptionHandler.showCustomError(pleaseCompleteAllFieldsText, result);
                break;
        }
    }

    private void handleClose() {
        openScreen("/view/start_screen.fxml", "Welcome");
    }

    private void openScreen(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("WOWTruyen - " + title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            closeStage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStage() {
        if (registerButton.getScene() != null) {
            ((Stage) registerButton.getScene().getWindow()).close();
        }
    }
}