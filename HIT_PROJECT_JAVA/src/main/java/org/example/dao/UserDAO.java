package org.example.dao;

import org.example.constant.MessageConstant;
import org.example.exception.DatabaseException;
import org.example.model.user.Role;
import org.example.model.user.Gender;
import org.example.model.user.User;

import java.sql.*;

public class UserDAO {

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));

                try {
                    user.setGender(Gender.valueOf(rs.getString("gender")));
                    user.setRole(Role.valueOf(rs.getString("role")));
                } catch (IllegalArgumentException | NullPointerException e) {
                    user.setGender(Gender.Other);
                    user.setRole(Role.USER);
                }

                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setFirstLogin(rs.getBoolean("is_first_login"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Lỗi truy vấn thông tin người dùng: " + e.getMessage(), e);
        }
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Lỗi kiểm tra email tồn tại", e);
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Lỗi kiểm tra username tồn tại", e);
        }
    }

    public void saveUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, role, gender, status, is_first_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole().toString());
            stmt.setString(6, user.getGender().toString());
            stmt.setString(7, user.getStatus());
            stmt.setBoolean(8, true); // Mặc định true

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("Không thể tạo người dùng mới, không có dòng nào thay đổi.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Lỗi khi lưu người dùng vào CSDL", e);
        }
    }
}