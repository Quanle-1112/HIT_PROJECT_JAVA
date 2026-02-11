package org.example.services.impl;

import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.DatabaseException;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.utils.EmailUtils;
import org.example.utils.OtpUtils;

public class IUserServiceImpl implements IUserService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean updateUserProfile(User user) {
        return userDAO.updateUserProfile(user);
    }

    @Override
    public boolean disableFirstLogin(int userId) {
        return userDAO.disableFirstLogin(userId);
    }

    @Override
    public boolean sendOtp(String email) {
        if (!userDAO.isEmailExist(email)) {
            return false;
        }

        String otp = OtpUtils.generateOtp();

        if (userDAO.updateOtp(email, otp)) {
            return EmailUtils.sendEmail(
                    email,
                    "WOWTruyen - Mã xác thực tài khoản",
                    "Mã OTP của bạn là: " + otp + "\nMã này sẽ hết hạn sau 5 phút."
            );
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

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
}