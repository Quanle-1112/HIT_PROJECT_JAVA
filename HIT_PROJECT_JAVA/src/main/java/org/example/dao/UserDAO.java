package org.example.dao;

import org.example.constant.MessageConstant;
import org.example.exception.DatabaseException;
import org.example.model.user.Gender;
import org.example.model.user.OtpStatus;
import org.example.model.user.Role;
import org.example.model.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));

        user.setPassword(rs.getString("password"));

        user.setFullName(rs.getString("full_name"));

        try {
            String genderStr = rs.getString("gender");
            user.setGender(genderStr != null ? Gender.valueOf(genderStr) : Gender.Other);
        } catch (IllegalArgumentException e) { user.setGender(Gender.Other); }

        try {
            String roleStr = rs.getString("role");
            user.setRole(roleStr != null ? Role.valueOf(roleStr) : Role.USER);
        } catch (IllegalArgumentException e) { user.setRole(Role.USER); }

        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPhoneNumber(rs.getString("phone_number"));

        user.setFirstLogin(rs.getBoolean("is_first_login"));

        return user;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
        return list;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, role, gender, status, is_first_login, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole() != null ? user.getRole().name() : Role.USER.name());
            stmt.setString(6, user.getGender() != null ? user.getGender().name() : Gender.Other.name());
            stmt.setString(7, "ACTIVE");
            stmt.setBoolean(8, true);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_SAVE, e);
        }
    }

    public boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateUserStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, gender = ?, phone_number = ?, avatar_url = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getGender().name());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getAvatarUrl());
            stmt.setInt(5, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateUserPassword(int userId, String newHashPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHashPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean disableFirstLogin(int userId) {
        String sql = "UPDATE users SET is_first_login = 0 WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateAvatar(int userId, String avatarUrl) {
        String sql = "UPDATE users SET avatar_url = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avatarUrl);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateOtp(String email, String otp) {
        String sql = "UPDATE users SET otp_code = ?, otp_expiry = DATE_ADD(NOW(), INTERVAL 5 MINUTE) WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, otp);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public void clearOtp(String email) {
        String sql = "UPDATE users SET otp_code = NULL, otp_expiry = NULL WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public OtpStatus checkOtpStatus(String email, String inputOtp) {
        String sql = "SELECT otp_expiry FROM users WHERE email = ? AND otp_code = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, inputOtp);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiry = rs.getTimestamp("otp_expiry");
                    if (expiry != null && expiry.after(new Timestamp(System.currentTimeMillis()))) {
                        return OtpStatus.SUCCESS;
                    }
                    return OtpStatus.EXPIRED_CODE;
                }
                return OtpStatus.INVALID_CODE;
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean deleteUser(int userId) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                int rows = stmt.executeUpdate();
                conn.commit();
                return rows > 0;
            }

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new DatabaseException(MessageConstant.ERR_DB_DELETE, e);
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    public boolean addEmailToBlacklist(String email) {
        String sql = "INSERT IGNORE INTO blacklisted_emails (email, banned_at) VALUES (?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_SAVE, e);
        }
    }

    public boolean removeEmailFromBlacklist(String email) {
        String sql = "DELETE FROM blacklisted_emails WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_DELETE, e);
        }
    }

    public boolean isEmailBlacklisted(String email) {
        String sql = "SELECT 1 FROM blacklisted_emails WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }
}