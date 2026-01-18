package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.Role;
import org.example.model.user.Sex;
import org.example.model.user.User;
import org.example.services.IEncryptorService;
import org.example.services.IRegisterService;

public class IRegisterServiceImpl implements IRegisterService {
    private final UserDAO userDAO = new UserDAO();
    private final IEncryptorService encryptorService = new IEncryptorServiceImpl();

    @Override
    public String register(User user, String confirmPassword) {
        if (userDAO.isEmailExist(user.getEmail())) {
            return "Email đã được đăng ký!";
        }

        if (userDAO.isUsernameExist(user.getUsername())) {
            return "Tên đăng nhập đã tồn tại!";
        }

        String hashedPassword = encryptorService.encrypt(user.getPassword());
        user.setPassword(hashedPassword);

        user.setRole(Role.USER);
        user.setGender(Sex.Other);
        user.setStatus("ACTIVE");

        boolean isSuccess = userDAO.saveUser(user);
        return isSuccess ? "SUCCESS" : "Lỗi hệ thống";
    }
}