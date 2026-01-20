package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.utils.SceneUtils;

public class StartScreenController {

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event ->
                SceneUtils.openNewWindow("/view/login.fxml", "Đăng nhập", loginButton));

        registerButton.setOnAction(event ->
                SceneUtils.openNewWindow("/view/register.fxml", "Đăng ký tài khoản", registerButton));
    }
}