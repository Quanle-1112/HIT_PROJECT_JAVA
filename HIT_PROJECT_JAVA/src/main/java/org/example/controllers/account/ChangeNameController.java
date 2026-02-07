package org.example.controllers.account;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

public class ChangeNameController {

    @FXML private TextField newNameField;
    @FXML private Label errorLabel;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            newNameField.setText(currentUser.getFullName());
        }

        if (errorLabel != null) errorLabel.setVisible(false);

        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> handleCancel());
    }

    private void handleSave() {
        String newName = newNameField.getText().trim();

        if (newName.isEmpty()) {
            showError("Tên không được để trống!");
            return;
        }
        if (newName.length() < 2) {
            showError("Tên quá ngắn!");
            return;
        }

        if (currentUser != null) {
            currentUser.setFullName(newName);

            boolean success = userDAO.updateUserProfile(currentUser);

            if (success) {
                SessionManager.getInstance().setCurrentUser(currentUser);

                handleCancel();
            } else {
                showError("Lỗi kết nối CSDL, vui lòng thử lại!");
            }
        }
    }

    private void handleCancel() {
        SceneUtils.switchScene(btnCancel, "/view/account/account_screen.fxml", "Tài khoản cá nhân");
    }

    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
        }
    }
}