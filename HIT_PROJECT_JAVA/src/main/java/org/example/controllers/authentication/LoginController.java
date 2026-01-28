package org.example.controllers.authentication;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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

    @FXML private TextField showPasswordTextField;
    @FXML private Button togglePasswordButton;
    @FXML private ImageView eyeIcon;

    private final ILoginService loginService = new ILoginServiceImpl();
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(invalidLoginText, pleaseCompleteAllFieldsText);

        if (showPasswordTextField != null && enterPasswordField != null) {
            showPasswordTextField.textProperty().bindBidirectional(enterPasswordField.textProperty());
        }

        loginButton.setOnAction(event -> handleLogin());

        cancelButton.setOnAction(event -> SceneUtils.openNewWindow("/view/read/start_screen.fxml", "Welcome", cancelButton));

        forgotPasswordButton.setOnAction(event -> SceneUtils.switchScene(forgotPasswordButton, "/view/authentication/forgot_password.fxml", "Forgot Password"));

        if (togglePasswordButton != null) {
            togglePasswordButton.setOnAction(event -> handleTogglePassword());
        }
    }

    private void handleTogglePassword() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            enterPasswordField.setVisible(false);
            showPasswordTextField.setVisible(true);

            if (eyeIcon != null) eyeIcon.setOpacity(1.0);
        } else {
            enterPasswordField.setVisible(true);
            showPasswordTextField.setVisible(false);

            if (eyeIcon != null) eyeIcon.setOpacity(0.5);
        }
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
                ConfirmInformationController controller = SceneUtils.switchScene(loginButton, "/view/authentication/confirm_information_screen.fxml", "Xác nhận thông tin");
                if (controller != null) controller.setCurrentUser(user);
            } else {
                SceneUtils.switchScene(loginButton, "/view/read/home_screen.fxml", "Trang chủ");
            }
        } else {
            UIExceptionHandler.showError(invalidLoginText);
        }
    }
}