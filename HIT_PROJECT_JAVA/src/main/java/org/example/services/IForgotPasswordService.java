package org.example.services;
import org.example.model.user.OtpStatus;

public interface IForgotPasswordService {
    OtpStatus sendOtp(String email);
    OtpStatus verifyOtp(String email, String otp);
    boolean resetPassword(String email, String newPassword);
}