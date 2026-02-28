package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.model.user.User;

public class ConfirmActionController {

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private Runnable onConfirmAction;

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> closeDialog());

        btnConfirm.setOnAction(e -> {
            if (onConfirmAction != null) {
                onConfirmAction.run();
            }
            closeDialog();
        });
    }

    public void initData(User user, boolean isBanning, Runnable onConfirmAction) {
        this.onConfirmAction = onConfirmAction;

        if (isBanning) {
            lblTitle.setText(MessageConstant.LOCK_ACCOUNT + user.getUsername() + "?");
            lblTitle.setTextFill(javafx.scene.paint.Color.web(MessageConstant.COLOR_1));
            lblMessage.setText(MessageConstant.EMAIL + user.getEmail() + MessageConstant.EMAIL_BLOCK);
            btnConfirm.setStyle(MessageConstant.COLOR_2);
            btnConfirm.setText(MessageConstant.LOCK_ACCOUNT_1);
        } else {
            lblTitle.setText(MessageConstant.UN_LOCK_ACCOUNT + user.getUsername() + "?");
            lblTitle.setTextFill(javafx.scene.paint.Color.web(MessageConstant.COLOR_3));
            lblMessage.setText(MessageConstant.REMOVE_FROM_BLACKLIST);
            btnConfirm.setStyle(MessageConstant.COLOR_4);
            btnConfirm.setText(MessageConstant.UN_LOCK);
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}