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
        // 1. Kiểm tra email có tồn tại không
        if (!userDAO.isEmailExist(email)) {
            return null;
        }

        // 2. Tạo OTP
        int otpValue = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(otpValue);

        // 3. Gửi Email
        String subject = "WOWTruyen - Reset Password";
        String body = "Mã xác thực của bạn là: " + otp + "\nVui lòng không chia sẻ mã này.";

        boolean sent = emailService.sendEmail(email, subject, body);
        return sent ? otp : null;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        // Mã hóa mật khẩu mới và lưu vào DB
        String hashedPassword = encryptorService.encrypt(newPassword);
        return userDAO.updatePasswordByEmail(email, hashedPassword);
    }
}