package org.example.services.impl;

import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AuthException;
import org.example.exception.DatabaseException;
import org.example.exception.NetworkException;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IForgotPasswordService;
import org.example.utils.EmailUtils;
import org.example.utils.EncryptionUtils;
import org.example.utils.OtpUtils;

public class IForgotPasswordServiceImpl implements IForgotPasswordService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void sendOtp(String email) {
        if (!userDAO.isEmailExist(email)) {
            throw new AuthException(MessageConstant.FORGOT_PASS_EMAIL_NOT_EXIST);
        }

        String otp = OtpUtils.generateOtp();

        if (userDAO.updateOtp(email, otp)) {
            boolean isSent = EmailUtils.sendEmail(
                    email,
                    "WOWTruyen - Khôi phục mật khẩu",
                    "Mã xác thực khôi phục mật khẩu của bạn là: " + otp + "\nVui lòng không chia sẻ mã này."
            );
            if (!isSent) {
                throw new NetworkException(MessageConstant.FORGOT_PASS_SEND_FAIL);
            }
        } else {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE);
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        OtpStatus status = userDAO.checkOtpStatus(email, otp);
        if (status == OtpStatus.INVALID_CODE) {
            throw new AuthException(MessageConstant.OTP_INVALID);
        }
        if (status == OtpStatus.EXPIRED_CODE) {
            throw new AuthException(MessageConstant.OTP_EXPIRED);
        }
        userDAO.clearOtp(email);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new AuthException(MessageConstant.ERR_USER_NOT_FOUND);
        }

        String hashedPassword = EncryptionUtils.hashPassword(newPassword);
        boolean success = userDAO.updateUserPassword(user.getId(), hashedPassword);

        if (!success) {
            throw new DatabaseException(MessageConstant.UPDATE_FAIL);
        }
    }
}