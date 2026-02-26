package org.example.controllers.account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.utils.EncryptionUtils;
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
        UIExceptionHandler.hideError(errorLabel);

        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> closeWindow());
    }

    private void handleSave() {
        UIExceptionHandler.hideError(errorLabel);

        String oldPass = oldPassField.getText();
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();

        if (ValidationUtils.areFieldsEmpty(oldPassField, newPassField, confirmPassField)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }

        if (!EncryptionUtils.verifyPassword(oldPass, currentUser.getPassword())) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.CHANGE_PASS_OLD_WRONG);
            return;
        }

        if (!ValidationUtils.isValidPassword(newPass)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_PASSWORD_MISMATCH);
            return;
        }

        btnSave.setDisable(true);
        btnSave.setText(MessageConstant.CONFIRM_LOADING);

        Task<Void> changePassTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String hashed = EncryptionUtils.hashPassword(newPass);
                if (!userDAO.updateUserPassword(currentUser.getId(), hashed)) {
                    throw new AppException(MessageConstant.UPDATE_FAIL);
                }
                currentUser.setPassword(hashed);
                return null;
            }
        };

        changePassTask.setOnSucceeded(e -> {
            redirectToLogin();
        });

        changePassTask.setOnFailed(e -> {
            btnSave.setDisable(false);
            btnSave.setText(MessageConstant.UPDATE_PASSWORD);

            Throwable ex = changePassTask.getException();
            UIExceptionHandler.showError(errorLabel, ex.getMessage());

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(changePassTask).start();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void redirectToLogin() {
        SessionManager.getInstance().logout();

        javafx.stage.Stage dialogStage = (javafx.stage.Stage) btnCancel.getScene().getWindow();
        javafx.stage.Window owner = dialogStage.getOwner();

        dialogStage.close();

        if (owner instanceof javafx.stage.Stage) {
            javafx.stage.Stage mainStage = (javafx.stage.Stage) owner;
            try {
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/authentication/login.fxml"));
                mainStage.setScene(new javafx.scene.Scene(root));
                mainStage.setTitle(MessageConstant.TITLE_LOGIN);
                mainStage.centerOnScreen();
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
                throw new AppException(MessageConstant.ERR_SYSTEM, ex);
            }
        }
    }
}