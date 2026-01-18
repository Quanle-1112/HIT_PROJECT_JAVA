package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    @FXML private Label usernameAlreadyExistsText;

    private final IRegisterService registerService = new IRegisterServiceImpl();

    @FXML
    public void initialize() {
        hideAllErrors();

        registerButton.setOnAction(event -> handleRegister());
        closeButton.setOnAction(event -> handleClose());
    }

    private void handleRegister() {
        hideAllErrors();

        String email = emailTextField.getText().trim();
        String username = usernameTextField.getText().trim();
        String pass = setPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            if (pleaseCompleteAllFieldsText != null) {
                pleaseCompleteAllFieldsText.setVisible(true);
            }
            return;
        }

        if (!pass.equals(confirmPass)) {
            if (passwordConfirmationMismatchText != null) {
                passwordConfirmationMismatchText.setVisible(true);
            }
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
                if (emailAlreadyRegisteredText != null) {
                    emailAlreadyRegisteredText.setVisible(true);
                }
                break;

            case "Tên đăng nhập đã tồn tại!":
                if (usernameAlreadyExistsText != null) {
                    usernameAlreadyExistsText.setVisible(true);
                } else if (emailAlreadyRegisteredText != null) {
                    emailAlreadyRegisteredText.setText("Username already exists!");
                    emailAlreadyRegisteredText.setVisible(true);
                }
                break;

            default:
                if (pleaseCompleteAllFieldsText != null) {
                    pleaseCompleteAllFieldsText.setText(result);
                    pleaseCompleteAllFieldsText.setVisible(true);
                }
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

    private void hideAllErrors() {
        if (pleaseCompleteAllFieldsText != null) {
            pleaseCompleteAllFieldsText.setVisible(false);
            pleaseCompleteAllFieldsText.setText("Please complete all fields!");
        }

        if (emailAlreadyRegisteredText != null) {
            emailAlreadyRegisteredText.setVisible(false);
            emailAlreadyRegisteredText.setText("Email already registered!");
        }

        if (passwordConfirmationMismatchText != null) {
            passwordConfirmationMismatchText.setVisible(false);
        }

        if (usernameAlreadyExistsText != null) {
            usernameAlreadyExistsText.setVisible(false);
        }
    }
}