package org.example.services;
import org.example.model.user.User;
public interface IUserService {
    boolean updateUserProfile(User user);
    boolean disableFirstLogin(int userId);
}