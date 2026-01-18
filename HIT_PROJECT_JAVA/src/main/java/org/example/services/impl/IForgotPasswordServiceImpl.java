package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.services.IEmailService;
import org.example.services.IEncryptorService;
import org.example.services.IForgotPasswordService;

import java.util.Random;

public class IForgotPasswordServiceImpl implements IForgotPasswordService {

    private final UserDAO userDAO = new UserDAO();
    private final IEmailService emailService = new IEmailServiceImpl();
    private final IEncryptorService encryptorService = new IEncryptorServiceImpl();

    @Override
    public String sendOtp(String email) {
        if (!userDAO.isEmailExist(email)) {
            return null;
        }

        int otpValue = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(otpValue);

        String subject = "WOWTruyen - Reset Password";
        String body = "Mã xác thực của bạn là: " + otp + "\nVui lòng không chia sẻ mã này.";

        boolean sent = emailService.sendEmail(email, subject, body);
        return sent ? otp : null;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        String hashedPassword = encryptorService.encrypt(newPassword);
        return userDAO.updatePasswordByEmail(email, hashedPassword);
    }
}