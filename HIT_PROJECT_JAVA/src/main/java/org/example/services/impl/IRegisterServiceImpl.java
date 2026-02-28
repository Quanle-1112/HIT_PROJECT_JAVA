package org.example.services.impl;

import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AuthException;
import org.example.exception.DatabaseException;
import org.example.model.user.Gender;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.services.IRegisterService;
import org.example.utils.EncryptionUtils;

public class IRegisterServiceImpl implements IRegisterService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void register(User user, String confirmPassword) {
        if (!user.getPassword().equals(confirmPassword)) {
            throw new AuthException(MessageConstant.REGISTER_PASSWORD_MISMATCH);
        }

        if (userDAO.isEmailExist(user.getEmail())) {
            throw new AuthException(MessageConstant.REGISTER_EMAIL_EXIST);
        }

        if (userDAO.isUsernameExist(user.getUsername())) {
            throw new AuthException(MessageConstant.REGISTER_USERNAME_EXIST);
        }

        try {
            String hashedPassword = EncryptionUtils.hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            user.setRole(Role.USER);
            user.setGender(Gender.Other);
            user.setStatus("ACTIVE");
            user.setFirstLogin(true);

            boolean success = userDAO.saveUser(user);
            if (!success) {
                throw new DatabaseException(MessageConstant.REGISTER_FAIL);
            }
        } catch (Exception e) {
            throw new DatabaseException(MessageConstant.ERR_DB_SAVE, e);
        }
    }
}