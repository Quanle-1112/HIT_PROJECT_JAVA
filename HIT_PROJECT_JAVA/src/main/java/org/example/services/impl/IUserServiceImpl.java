package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IEmailService;
import org.example.services.IUserService;
import java.util.Random;

public class IUserServiceImpl implements IUserService {
    private final UserDAO userDAO = new UserDAO();
    private final IEmailService emailService = new IEmailServiceImpl();

    @Override public boolean updateUserProfile(User user) { return userDAO.updateUser(user); }
    @Override public boolean disableFirstLogin(int userId) { return userDAO.disableFirstLogin(userId); }

    @Override
    public boolean sendOtp(String email) {
        int otpValue = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(otpValue);

        if (userDAO.updateOtp(email, otp)) {
            return emailService.sendEmail(email, "WOWTruyen - Mã xác thực thông tin", "Mã xác thực: " + otp + "\nMã hết hạn sau 5 phút.");
        }
        return false;
    }

    @Override
    public OtpStatus verifyOtp(String email, String otp) {
        OtpStatus status = userDAO.checkOtpStatus(email, otp);
        if (status == OtpStatus.SUCCESS) {
            userDAO.clearOtp(email);
        }
        return status;
    }
}