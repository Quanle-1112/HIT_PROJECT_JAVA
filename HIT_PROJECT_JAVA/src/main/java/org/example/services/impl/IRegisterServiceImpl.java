package org.example.services.impl;

import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.model.user.Role;
import org.example.model.user.Gender;
import org.example.model.user.User;
import org.example.services.IRegisterService;
import org.example.utils.EncryptionUtils;

public class IRegisterServiceImpl implements IRegisterService {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public String register(User user, String confirmPassword) {
        if (userDAO.isEmailExist(user.getEmail())) {
            return MessageConstant.REGISTER_EMAIL_EXIST;
        }

        if (userDAO.isUsernameExist(user.getUsername())) {
            return MessageConstant.REGISTER_USERNAME_EXIST;
        }

        String hashedPassword = EncryptionUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        user.setRole(Role.USER);
        user.setGender(Gender.Other);
        user.setStatus("ACTIVE");

        boolean isSuccess = userDAO.saveUser(user);
        return isSuccess ? MessageConstant.REGISTER_SUCCESS : MessageConstant.REGISTER_FAIL;
    }
}