package org.example.controllers.account;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.constant.MessageConstant;
import javafx.scene.control.PasswordField;
import org.example.dao.UserDAO;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.utils.EncryptionUtils;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;
import org.example.utils.ValidationUtils;

public class ChangePasswordController {

    @FXML private PasswordField oldPassField;

    @FXML private PasswordField newPassField;

    @FXML private PasswordField confirmPassField;

    @FXML private Label errorLabel;

    @FXML private Button btnSave;

    @FXML private Button btnCancel;

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
            showError(MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }

        if (!EncryptionUtils.verifyPassword(oldPass, currentUser.getPassword())) {
            showError(MessageConstant.CHANGE_PASS_OLD_WRONG);
            return;
        }

        if (!ValidationUtils.isValidPassword(newPass)) {
            showError(MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError(MessageConstant.REGISTER_PASSWORD_MISMATCH);
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
                showError(MessageConstant.UPDATE_FAIL);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError(MessageConstant.ERR_SYSTEM);
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