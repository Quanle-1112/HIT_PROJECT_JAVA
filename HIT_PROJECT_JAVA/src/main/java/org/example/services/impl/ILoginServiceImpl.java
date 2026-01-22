package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.services.ILoginService;
import org.example.utils.EncryptionUtils;

public class ILoginServiceImpl implements ILoginService {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (EncryptionUtils.verifyPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
}