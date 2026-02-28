package org.example.controllers.account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
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
        UIExceptionHandler.hideError(errorLabel);

        if (currentUser != null) {
            newNameField.setText(currentUser.getFullName());
        }

        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> closeWindow());
    }

    private void handleSave() {
        UIExceptionHandler.hideError(errorLabel);
        String newName = newNameField.getText().trim();

        if (newName.isEmpty()) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.VALIDATION_NAME_EMPTY);
            return;
        }
        if (newName.length() < 2 || newName.length() > 50) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.VALIDATION_NAME_SHORT);
            return;
        }

        btnSave.setDisable(true);
        btnSave.setText(MessageConstant.CONFIRM_LOADING);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                currentUser.setFullName(newName);
                if (!userDAO.updateUserProfile(currentUser)) {
                    throw new AppException(MessageConstant.UPDATE_FAIL);
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            SessionManager.getInstance().setCurrentUser(currentUser);
            closeWindow();
        });

        saveTask.setOnFailed(e -> {
            btnSave.setDisable(false);
            btnSave.setText(MessageConstant.SAVE);

            Throwable ex = saveTask.getException();
            UIExceptionHandler.showError(errorLabel, ex.getMessage());

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(saveTask).start();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}