package org.example.controllers.authentication;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.services.impl.ILoginServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;
import org.example.utils.ValidationUtils;

public class LoginController {

    @FXML private Button cancelButton, loginButton, forgotPasswordButton;
    @FXML private PasswordField enterPasswordField;
    @FXML private TextField usernameTextField;
    @FXML private Label errorLabel;
    @FXML private TextField showPasswordTextField;
    @FXML private Button togglePasswordButton;
    @FXML private ImageView eyeIcon;

    private final ILoginService loginService = new ILoginServiceImpl();
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);

        if (showPasswordTextField != null && enterPasswordField != null) {
            showPasswordTextField.textProperty().bindBidirectional(enterPasswordField.textProperty());
        }

        loginButton.setOnAction(event -> handleLogin());
        enterPasswordField.setOnAction(event -> handleLogin());
        usernameTextField.setOnAction(event -> handleLogin());

        cancelButton.setOnAction(event ->
                SceneUtils.switchScene(cancelButton, "/view/read/start_screen.fxml", MessageConstant.TITLE_START)
        );

        forgotPasswordButton.setOnAction(event ->
                SceneUtils.switchScene(forgotPasswordButton, "/view/authentication/forgot_password.fxml", MessageConstant.TITLE_FORGOT_PASS)
        );

        if (togglePasswordButton != null) {
            togglePasswordButton.setOnAction(event -> togglePasswordVisibility());
        }
    }

    private void togglePasswordVisibility() {
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
        UIExceptionHandler.hideError(errorLabel);

        if (ValidationUtils.areFieldsEmpty(usernameTextField, enterPasswordField)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }

        String username = usernameTextField.getText().trim();
        String password = isPasswordVisible ? showPasswordTextField.getText() : enterPasswordField.getText();

        loginButton.setDisable(true);
        loginButton.setText(MessageConstant.LOGIN_SUCCESS);

        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return loginService.authenticate(username, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            SessionManager.getInstance().setCurrentUser(user);

            if (user.getRole() == Role.ADMIN) {
                SceneUtils.switchScene(
                        loginButton,
                        "/view/admin/admin_dashboard.fxml",
                        "WOWTruyen - Administrator"
                );
            } else {
                if (user.isFirstLogin()) {
                    ConfirmInformationController controller = SceneUtils.switchScene(
                            loginButton,
                            "/view/authentication/confirm_information_screen.fxml",
                            MessageConstant.TITLE_CONFIRM_INFO
                    );
                    if (controller != null) controller.setCurrentUser(user);
                } else {
                    SceneUtils.switchScene(
                            loginButton,
                            "/view/read/home_screen.fxml",
                            MessageConstant.TITLE_HOME
                    );
                }
            }
        });

        loginTask.setOnFailed(e -> {
            loginButton.setDisable(false);
            loginButton.setText(MessageConstant.TITLE_LOGIN);

            Throwable ex = loginTask.getException();

            if (ex instanceof AuthException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else if (ex instanceof AppException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(loginTask).start();
    }
}