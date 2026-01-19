package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.services.impl.ILoginServiceImpl;

import java.io.IOException;

public class LoginController {

    @FXML private Button cancelButton;
    @FXML private PasswordField enterPasswordField;
    @FXML private Button loginButton;
    @FXML private TextField usernameTextField;
    @FXML private Button forgotPasswordButton;

    // Các label lỗi khớp fx:id [cite: 162, 165]
    @FXML private Label invalidLoginText;
    @FXML private Label pleaseCompleteAllFieldsText;

    private final ILoginService loginService = new ILoginServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(invalidLoginText, pleaseCompleteAllFieldsText);

        loginButton.setOnAction(event -> handleLogin());
        forgotPasswordButton.setOnAction(event -> handleForgotPassword());
        cancelButton.setOnAction(event -> openScreen("/view/start_screen.fxml", "Welcome"));
    }

    private void handleLogin() {
        UIExceptionHandler.hideError(invalidLoginText, pleaseCompleteAllFieldsText);

        String username = usernameTextField.getText().trim();
        String password = enterPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        User user = loginService.authenticate(username, password);

        if (user != null) {
            if (user.isFirstLogin()) {
                openConfirmInfoScreen(user);
            } else {
                openHomeScreen();
            }
        } else {
            UIExceptionHandler.showError(invalidLoginText);
        }
    }

    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forgot_password.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) forgotPasswordButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - Forgot Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openConfirmInfoScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/confirm_information_screen.fxml"));
            Parent root = loader.load();
            ConfirmInformationController controller = loader.getController();
            controller.setCurrentUser(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - Xác nhận thông tin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openHomeScreen() {
        openScreen("/view/home_screen.fxml", "Trang chủ");
    }

    private void openScreen(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}