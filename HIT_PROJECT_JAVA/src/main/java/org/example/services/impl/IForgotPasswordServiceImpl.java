package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.OtpStatus;
import org.example.services.IForgotPasswordService;
import org.example.utils.EmailUtils;
import org.example.utils.EncryptionUtils;
import org.example.utils.OtpUtils;

public class IForgotPasswordServiceImpl implements IForgotPasswordService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public OtpStatus sendOtp(String email) {
        if (!userDAO.isEmailExist(email)) return OtpStatus.EMAIL_NOT_EXIST;

        String otp = OtpUtils.generateOtp();

        if (userDAO.updateOtp(email, otp)) {
            boolean sent = EmailUtils.sendEmail(
                    email,
                    "WOWTruyen - Reset Password",
                    "Mã xác thực: " + otp + "\nMã hết hạn sau 5 phút."
            );
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
        String hashedPassword = EncryptionUtils.hashPassword(newPassword);
        return userDAO.updatePasswordByEmail(email, hashedPassword);
    }
}