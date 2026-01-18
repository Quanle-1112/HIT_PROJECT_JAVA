package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.services.IEncryptorService;
import org.example.services.ILoginService;

public class ILoginServiceImpl implements ILoginService {
    private final UserDAO userDAO = new UserDAO();
    private final IEncryptorService encryptorService = new IEncryptorServiceImpl();

    @Override
    public User authenticate(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (encryptorService.checkPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
}