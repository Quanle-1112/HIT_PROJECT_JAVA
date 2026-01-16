package org.example.services.impl;
import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.services.IUserService;

public class IUserServiceImpl implements IUserService {
    private final UserDAO userDAO = new UserDAO();
    @Override public boolean updateUserProfile(User user) { return userDAO.updateUser(user); }
    @Override public boolean disableFirstLogin(int userId) { return userDAO.disableFirstLogin(userId); }
}