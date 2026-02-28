package org.example.controllers.read;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.constant.MessageConstant;
import org.example.utils.SceneUtils;

public class StartScreenController {

    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event ->
                SceneUtils.switchScene(loginButton, "/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN)
        );

        registerButton.setOnAction(event ->
                SceneUtils.switchScene(registerButton, "/view/authentication/register.fxml", MessageConstant.TITLE_REGISTER)
        );
    }
}