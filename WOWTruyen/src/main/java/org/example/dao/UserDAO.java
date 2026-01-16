package org.example.dao;

import org.example.model.user.Role;
import org.example.model.user.Sex;
import org.example.model.user.User;
import java.sql.*;

public class UserDAO {

    // --- CÁC HÀM CŨ (Giữ nguyên) ---
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean isUsernameExist(String username) {
        return checkExist("SELECT COUNT(*) FROM users WHERE username = ?", username);
    }

    public boolean isEmailExist(String email) {
        return checkExist("SELECT COUNT(*) FROM users WHERE email = ?", email);
    }

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, email, password, full_name, role, status, gender, is_first_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole().toString());
            stmt.setString(6, user.getStatus());
            stmt.setString(7, user.getGender().toString());
            stmt.setInt(8, 1);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, gender = ?, avatar_url = ?, phone_number = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getGender().toString());
            stmt.setString(4, user.getAvatarUrl());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setInt(6, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean disableFirstLogin(int userId) {
        String sql = "UPDATE users SET is_first_login = 0 WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- HÀM MỚI CHO FORGOT PASSWORD ---
    public boolean updatePasswordByEmail(String email, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHashedPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- HELPER METHODS ---
    private boolean checkExist(String sql, String param) {
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setFirstLogin(rs.getBoolean("is_first_login"));
        try { user.setRole(Role.valueOf(rs.getString("role"))); } catch (Exception e) { user.setRole(Role.USER); }
        try { user.setGender(Sex.valueOf(rs.getString("gender"))); } catch (Exception e) { user.setGender(Sex.Other); }
        return user;
    }
}