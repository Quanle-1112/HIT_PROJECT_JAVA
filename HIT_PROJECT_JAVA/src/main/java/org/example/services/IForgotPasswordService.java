package org.example.services;

public interface IForgotPasswordService {
    String sendOtp(String email);
    boolean resetPassword(String email, String newPassword);
}