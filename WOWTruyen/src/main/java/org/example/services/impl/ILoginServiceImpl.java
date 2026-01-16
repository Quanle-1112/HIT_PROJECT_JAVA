package org.example.services.impl;

import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.services.IEncryptorService;
import org.example.services.ILoginService;

public class ILoginServiceImpl implements ILoginService {
    // Service gọi DAO, KHÔNG để Controller gọi DAO
    private final UserDAO userDAO = new UserDAO();
    private final IEncryptorService encryptorService = new IEncryptorServiceImpl();

    @Override
    public User authenticate(String username, String password) {
        // 1. Gọi DAO lấy thông tin
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            return null; // Không tìm thấy user
        }

        // 2. Kiểm tra mật khẩu (Logic bảo mật nằm ở đây)
        if (encryptorService.checkPassword(password, user.getPassword())) {
            return user; // Đúng pass
        } else {
            return null; // Sai pass
        }
    }
}