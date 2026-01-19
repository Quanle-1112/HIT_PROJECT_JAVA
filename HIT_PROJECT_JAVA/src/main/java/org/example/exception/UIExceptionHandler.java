package org.example.exception;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class UIExceptionHandler {

    public static void showError(Label label, ErrorType errorType) {
        if (label != null) {
            label.setText(errorType.getMessage());
            label.setVisible(true);
        }
    }

    public static void showSuccess(Label label, ErrorType successType) {
        if (label != null) {
            label.setText(successType.getMessage());
            label.setVisible(true);
        }
    }

    public static void showCustomError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
        }
    }

    public static void hideError(Label... labels) {
        for (Label label : labels) {
            if (label != null) {
                label.setVisible(false);
            }
        }
    }
}