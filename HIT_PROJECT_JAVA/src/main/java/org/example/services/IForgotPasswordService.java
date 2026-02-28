package org.example.services;

public interface IForgotPasswordService {
    void sendOtp(String email);
    void verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword);
}