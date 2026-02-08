package org.example.controllers.account;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.example.dao.UserDAO;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.utils.EncryptionUtils;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;
import org.example.utils.ValidationUtils;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPassField;

    @FXML
    private PasswordField newPassField;

    @FXML
    private PasswordField confirmPassField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (errorLabel != null) {
            UIExceptionHandler.hideError(errorLabel);
        }

        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
    }

    private void handleSave() {
        UIExceptionHandler.hideError(errorLabel);


        if (currentUser == null) {
            return;
        }

        String oldPass = oldPassField.getText();
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();

        if (ValidationUtils.areFieldsEmpty(oldPassField, newPassField, confirmPassField)) {
            showError("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!EncryptionUtils.verifyPassword(oldPass, currentUser.getPassword())) {
            showError("Mật khẩu hiện tại không chính xác!");
            return;
        }

        if (!ValidationUtils.isValidPassword(newPass)) {
            showError("Mật khẩu mới phải từ 6-20 ký tự, bao gồm chữ hoa, thường, số và ký tự đặc biệt!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            String hashedPassword = EncryptionUtils.hashPassword(newPass);

            currentUser.setPassword(hashedPassword);

            boolean isUpdated = userDAO.updateUserPassword(currentUser.getId(), hashedPassword);

            if (isUpdated) {
                SessionManager.getInstance().setCurrentUser(currentUser);

                SceneUtils.switchScene(btnSave, "/view/account/account_screen.fxml", "Tài khoản cá nhân");
            } else {
                showError("Lỗi hệ thống: Không thể cập nhật mật khẩu. Vui lòng thử lại sau!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Đã xảy ra lỗi không mong muốn!");
        }
    }

    private void handleCancel() {
        SceneUtils.switchScene(btnCancel, "/view/account/account_screen.fxml", "Tài khoản cá nhân");
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            UIExceptionHandler.showError(errorLabel);
        }
    }
}