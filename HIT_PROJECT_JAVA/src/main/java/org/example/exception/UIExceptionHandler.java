package org.example.exception;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.example.constant.MessageConstant;

public class UIExceptionHandler {

    public static void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
            label.setStyle("-fx-text-fill: red;");
        }
    }

    public static void handle(Exception e, Label label) {
        e.printStackTrace();

        String message;
        if (e instanceof DatabaseException) {
            message = MessageConstant.ERR_DB_CONNECT;
        } else if (e instanceof NetworkException) {
            message = MessageConstant.ERR_NETWORK;
        } else if (e instanceof AuthException) {
            message = e.getMessage();
        } else {
            message = MessageConstant.ERR_SYSTEM;
        }

        Platform.runLater(() -> showError(label, message));
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static void hideError(Label... labels) {
        for (Label label : labels) {
            if (label != null) {
                label.setVisible(false);
            }
        }
    }
}