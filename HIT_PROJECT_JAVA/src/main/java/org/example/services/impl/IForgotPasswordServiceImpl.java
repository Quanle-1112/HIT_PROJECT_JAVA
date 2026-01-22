package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.OtpStatus;
import org.example.services.IEmailService;
import org.example.services.IForgotPasswordService;
import org.example.utils.EncryptionUtils;
import java.util.Random;

public class IForgotPasswordServiceImpl implements IForgotPasswordService {

    private final UserDAO userDAO = new UserDAO();
    private final IEmailService emailService = new IEmailServiceImpl();

    @Override
    public OtpStatus sendOtp(String email) {
        if (!userDAO.isEmailExist(email)) return OtpStatus.EMAIL_NOT_EXIST;

        int otpValue = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(otpValue);

        if (userDAO.updateOtp(email, otp)) {
            boolean sent = emailService.sendEmail(email, "WOWTruyen - Reset Password", "Mã xác thực: " + otp + "\nMã hết hạn sau 5 phút.");
            return sent ? OtpStatus.SUCCESS : OtpStatus.SEND_FAILED;
        }
        return OtpStatus.SEND_FAILED;
    }

    @Override
    public OtpStatus verifyOtp(String email, String otp) {
        OtpStatus status = userDAO.checkOtpStatus(email, otp);
        if (status == OtpStatus.SUCCESS) {
            userDAO.clearOtp(email);
        }
        return status;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        // Sử dụng trực tiếp EncryptionUtils thay cho encryptorService
        String hashedPassword = EncryptionUtils.hashPassword(newPassword);
        return userDAO.updatePasswordByEmail(email, hashedPassword);
    }
}