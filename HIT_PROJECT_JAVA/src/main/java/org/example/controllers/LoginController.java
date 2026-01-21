package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.services.impl.ILoginServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

public class LoginController {
    @FXML private Button cancelButton, loginButton, forgotPasswordButton;
    @FXML private PasswordField enterPasswordField;
    @FXML private TextField usernameTextField;
    @FXML private Label invalidLoginText, pleaseCompleteAllFieldsText;

    private final ILoginService loginService = new ILoginServiceImpl();

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(invalidLoginText, pleaseCompleteAllFieldsText);

        loginButton.setOnAction(event -> handleLogin());
        forgotPasswordButton.setOnAction(event -> SceneUtils.switchScene(forgotPasswordButton, "/view/forgot_password.fxml", "Forgot Password"));
        cancelButton.setOnAction(event -> SceneUtils.openNewWindow("/view/start_screen.fxml", "Welcome", cancelButton));
    }

    private void handleLogin() {
        UIExceptionHandler.hideError(invalidLoginText, pleaseCompleteAllFieldsText);

        if (ValidationUtils.areFieldsEmpty(usernameTextField, enterPasswordField)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        User user = loginService.authenticate(usernameTextField.getText().trim(), enterPasswordField.getText());

        if (user != null) {
            if (user.isFirstLogin()) {
                ConfirmInformationController controller = SceneUtils.switchScene(loginButton, "/view/confirm_information_screen.fxml", "Xác nhận thông tin");
                if (controller != null) controller.setCurrentUser(user);
            } else {
                SceneUtils.switchScene(loginButton, "/view/home_screen.fxml", "Trang chủ");
            }
        } else {
            UIExceptionHandler.showError(invalidLoginText);
        }
    }
}