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

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
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
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
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
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
        return users;
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
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

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, role, gender, status, is_first_login, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getRole() != null ? user.getRole().toString() : Role.USER.toString());
            stmt.setString(6, user.getGender() != null ? user.getGender().toString() : Gender.Other.toString());
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

    public boolean updateUserProfile(User user) {
        String sql = "UPDATE users SET full_name = ?, gender = ?, phone_number = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getGender().toString());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateUserPassword(int userId, String newHashPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHashPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_UPDATE, e);
        }
    }

    public boolean updateAvatar(int userId, String avatarPath) {
        String sql = "UPDATE users SET avatar_url = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, avatarPath);
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

    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
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
                    } else {
                        return OtpStatus.EXPIRED_CODE;
                    }
                } else {
                    return OtpStatus.INVALID_CODE;
                }
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

            try (PreparedStatement stmtHistory = conn.prepareStatement("DELETE FROM user_history WHERE user_id = ?")) {
                stmtHistory.setInt(1, userId);
                stmtHistory.executeUpdate();
            }

            try (PreparedStatement stmtFav = conn.prepareStatement("DELETE FROM user_favorites WHERE user_id = ?")) {
                stmtFav.setInt(1, userId);
                stmtFav.executeUpdate();
            }

            int rowsUser;
            try (PreparedStatement stmtUser = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                stmtUser.setInt(1, userId);
                rowsUser = stmtUser.executeUpdate();
            }

            conn.commit();
            return rowsUser > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new DatabaseException(MessageConstant.ERR_DB_DELETE, e);
        } finally {
            DBConnect.closeConnection(conn);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));

        try {
            String genderStr = rs.getString("gender");
            user.setGender(genderStr != null ? Gender.valueOf(genderStr) : Gender.Other);
        } catch (IllegalArgumentException e) {
            user.setGender(Gender.Other);
        }

        try {
            String roleStr = rs.getString("role");
            user.setRole(roleStr != null ? Role.valueOf(roleStr) : Role.USER);
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
        }

        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setFirstLogin(rs.getBoolean("is_first_login"));

        return user;
    }
}