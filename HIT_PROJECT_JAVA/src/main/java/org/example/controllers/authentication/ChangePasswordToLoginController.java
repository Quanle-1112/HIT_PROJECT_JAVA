package org.example.controllers.authentication;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class ChangePasswordToLoginController {
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Button updateButton, cancelButton;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    public void setUserEmail(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        updateButton.setOnAction(event -> handleUpdate());
        cancelButton.setOnAction(event ->
                SceneUtils.switchScene(cancelButton, "/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN)
        );
    }

    private void handleUpdate() {
        UIExceptionHandler.hideError(errorLabel);

        if (ValidationUtils.areFieldsEmpty(newPasswordField, confirmPasswordField)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }
        if (!ValidationUtils.isValidPassword(newPasswordField.getText())) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_PASSWORD_INVALID);
            return;
        }
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.REGISTER_PASSWORD_MISMATCH);
            return;
        }

        updateButton.setDisable(true);
        updateButton.setText("Đang cập nhật...");

        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.resetPassword(userEmail, newPasswordField.getText());
                return null;
            }
        };

        updateTask.setOnSucceeded(e -> {
            UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, MessageConstant.REGISTER_SUCCESS, MessageConstant.CHANGE_PASS_SUCCESS);
            SceneUtils.switchScene(updateButton, "/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN);
        });

        updateTask.setOnFailed(e -> {
            updateButton.setDisable(false);
            updateButton.setText("Update Password");

            Throwable ex = updateTask.getException();
            if (ex instanceof AppException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(updateTask).start();
    }
}