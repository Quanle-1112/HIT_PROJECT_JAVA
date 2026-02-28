package org.example.services.impl;

import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AuthException;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.utils.EncryptionUtils;

public class ILoginServiceImpl implements ILoginService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            throw new AuthException(MessageConstant.LOGIN_FAIL);
        }

        if (!EncryptionUtils.verifyPassword(password, user.getPassword())) {
            throw new AuthException(MessageConstant.LOGIN_FAIL);
        }

        if ("BANNED".equalsIgnoreCase(user.getStatus())) {
            throw new AuthException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }
}