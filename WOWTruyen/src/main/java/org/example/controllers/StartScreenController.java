package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreenController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    public void initialize() {
        // Sự kiện khi bấm nút Login
        loginButton.setOnAction(event -> openScreen("/view/login.fxml", "Đăng nhập"));

        // Sự kiện khi bấm nút Register
        registerButton.setOnAction(event -> openScreen("/view/register.fxml", "Đăng ký tài khoản"));
    }

    // Hàm chung để mở màn hình mới và đóng màn hình hiện tại (Start Screen)
    private void openScreen(String fxmlPath, String title) {
        try {
            // Tải file FXML tương ứng
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Tạo Stage mới
            Stage stage = new Stage();
            stage.setTitle("WOWTruyen - " + title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            // Đóng màn hình Start Screen hiện tại
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi: Không thể tải file " + fxmlPath);
        }
    }
}