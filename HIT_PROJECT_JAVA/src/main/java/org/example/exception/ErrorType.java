package org.example.exception;

public enum ErrorType {
    PLEASE_COMPLETE_ALL_FIELDS("Please complete all fields!"),

    INVALID_LOGIN("Invalid login. Please try again!"),

    EMAIL_ALREADY_REGISTERED("Email already registered!"),
    USERNAME_ALREADY_EXISTS("Username already exists!"),
    PASSWORD_MISMATCH("Password confirmation mismatch!"),
    EMAIL_NOT_FOUND("This email address is not registered in our system!"),
    INVALID_CODE("Invalid code. Please try again."),
    CHECK_TERMS_BOX("Please check the box to agree to the terms and conditions"),
    FAIL_TO_SEND_EMAIL("Unable to send email. Please verify your network connection"),
    CODE_SENT_SUCCESS("Verification code sent to your email");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}