package org.example.utils;

import javafx.scene.control.TextInputControl;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,10}$";
    public static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String PHONE_PATTERN = "^0\\d{9}$";

    public static boolean areFieldsEmpty(TextInputControl... controls) {
        for (TextInputControl control : controls) {
            if (control.getText() == null || control.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    public static boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_PATTERN, password);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.matches(PHONE_PATTERN, phone);
    }
}