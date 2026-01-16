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

    // Các Label lỗi
    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Label emailAlreadyRegisteredText;
    @FXML private Label passwordConfirmationMismatchText;
    @FXML private Label usernameAlreadyExistsText; // Đảm bảo bên FXML đã có fx:id này

    // Service
    private final IRegisterService registerService = new IRegisterServiceImpl();

    @FXML
    public void initialize() {
        // Gọi hàm ẩn ngay khi mở form
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

        // 1. Check rỗng
        if (email.isEmpty() || username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            if (pleaseCompleteAllFieldsText != null) {
                pleaseCompleteAllFieldsText.setVisible(true);
            }
            return;
        }

        // 2. Check khớp pass
        if (!pass.equals(confirmPass)) {
            if (passwordConfirmationMismatchText != null) {
                passwordConfirmationMismatchText.setVisible(true);
            }
            return;
        }

        // 3. Gọi Service
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(pass);
        newUser.setFullName(username);

        String result = registerService.register(newUser, confirmPass);

        // 4. Xử lý kết quả
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
                    // Fallback nếu chưa tạo label riêng
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

    // Hàm ẩn lỗi an toàn (Kiểm tra null trước khi ẩn)
    private void hideAllErrors() {
        if (pleaseCompleteAllFieldsText != null) {
            pleaseCompleteAllFieldsText.setVisible(false);
            pleaseCompleteAllFieldsText.setText("Please complete all fields!"); // Reset text gốc
        }

        if (emailAlreadyRegisteredText != null) {
            emailAlreadyRegisteredText.setVisible(false);
            emailAlreadyRegisteredText.setText("Email already registered!"); // Reset text gốc
        }

        if (passwordConfirmationMismatchText != null) {
            passwordConfirmationMismatchText.setVisible(false);
        }

        if (usernameAlreadyExistsText != null) {
            usernameAlreadyExistsText.setVisible(false);
        }
    }
}