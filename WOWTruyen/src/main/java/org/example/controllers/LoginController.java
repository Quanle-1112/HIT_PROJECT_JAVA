package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.services.impl.ILoginServiceImpl;

import java.io.IOException;

public class LoginController {

    @FXML private Button cancelButton;
    @FXML private PasswordField enterPasswordField;
    @FXML private Button loginButton;
    @FXML private TextField usernameTextField;
    @FXML private Button forgotPasswordButton; // Nút Quên mật khẩu

    @FXML private Label invalidLoginText;
    @FXML private Label pleaseCompleteAllFieldsText;

    private final ILoginService loginService = new ILoginServiceImpl();

    @FXML
    public void initialize() {
        hideAllErrors();

        // Sự kiện đăng nhập
        loginButton.setOnAction(event -> handleLogin());

        // Sự kiện chuyển sang màn hình Forgot Password (BỎ QUA THÔNG BÁO)
        forgotPasswordButton.setOnAction(event -> handleForgotPassword());

        // Sự kiện hủy bỏ
        cancelButton.setOnAction(event -> openScreen("/view/start_screen.fxml", "Welcome"));
    }

    private void handleLogin() {
        hideAllErrors();
        String username = usernameTextField.getText().trim();
        String password = enterPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(true);
            return;
        }

        User user = loginService.authenticate(username, password);

        if (user != null) {
            // Kiểm tra trạng thái đăng nhập lần đầu để điều hướng
            if (user.isFirstLogin()) {
                openConfirmInfoScreen(user);
            } else {
                openHomeScreen();
            }
        } else {
            if (invalidLoginText != null) invalidLoginText.setVisible(true);
        }
    }

    // --- PHẦN SỬA ĐỔI CHÍNH: CHUYỂN GIAO DIỆN QUÊN MẬT KHẨU ---
    private void handleForgotPassword() {
        try {
            // Tải file FXML của giao diện Forgot Password
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forgot_password.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại từ nút đã bấm
            Stage stage = (Stage) forgotPasswordButton.getScene().getWindow();

            // Thay đổi Scene của Stage hiện tại (không mở cửa sổ mới)
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - Forgot Password");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi: Không tìm thấy file forgot_password.fxml");
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

    private void hideAllErrors() {
        if (invalidLoginText != null) invalidLoginText.setVisible(false);
        if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(false);
    }
}