package org.example.exception;

import javafx.scene.control.Label;

public class UIExceptionHandler {

    public static void showError(Label label) {
        if (label != null) {
            label.setVisible(true);
        }
    }

    public static void showSuccess(Label label) {
        if (label != null) {
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