package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import java.io.IOException;

public class ForgotPasswordController {
    @FXML private TextField emailToResetPasswordText;
    @FXML private Label emailNotFoundText;
    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Button sendRecoveryCodeButton;
    @FXML private Button backToLoginButton;

    private final IForgotPasswordService forgotPasswordService = new IForgotPasswordServiceImpl();

    @FXML
    public void initialize() {
        hideAllErrors(); // Ẩn ngay khi giao diện hiện ra [cite: 1]
        sendRecoveryCodeButton.setOnAction(event -> handleSendCode());
        backToLoginButton.setOnAction(event -> navigateToLogin());
    }

    private void handleSendCode() {
        hideAllErrors(); // Ẩn lỗi cũ trước khi check mới [cite: 21]
        String email = emailToResetPasswordText.getText().trim();

        if (email.isEmpty()) {
            pleaseCompleteAllFieldsText.setVisible(true); // Chỉ hiện 1 lỗi [cite: 22]
            return;
        }

        String otp = forgotPasswordService.sendOtp(email);
        if (otp != null) {
            navigateToConfirmCode(email, otp); // Chuyển cảnh trực tiếp [cite: 25]
        } else {
            emailNotFoundText.setVisible(true); // Hiện lỗi đơn lẻ [cite: 31]
        }
    }

    private void navigateToLogin() {
        try {
            // Đảm bảo đường dẫn FXML chính xác với cấu trúc project của bạn
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToConfirmCode(String email, String otp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/confirm_verify_code.fxml"));
            Parent root = loader.load();
            ConfirmVerifyCodeController controller = loader.getController();
            controller.setInitData(email, otp);
            Stage stage = (Stage) sendRecoveryCodeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void hideAllErrors() {
        emailNotFoundText.setVisible(false);
        pleaseCompleteAllFieldsText.setVisible(false);
    }
}