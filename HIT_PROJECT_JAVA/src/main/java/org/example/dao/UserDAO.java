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

    // ==========================================
    // 1. MAP DỮ LIỆU TỪ DB SANG JAVA (QUAN TRỌNG: Mapping đúng tên cột SQL)
    // ==========================================
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        // Sửa: Lấy cột "user_id" thay vì "id"
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));

        // Sửa: Lấy cột "password" thay vì "password_hash"
        user.setPassword(rs.getString("password"));

        user.setFullName(rs.getString("full_name"));

        // Xử lý Enum an toàn
        try {
            String genderStr = rs.getString("gender");
            user.setGender(genderStr != null ? Gender.valueOf(genderStr) : Gender.Other);
        } catch (IllegalArgumentException e) { user.setGender(Gender.Other); }

        try {
            String roleStr = rs.getString("role");
            user.setRole(roleStr != null ? Role.valueOf(roleStr) : Role.USER);
        } catch (IllegalArgumentException e) { user.setRole(Role.USER); }

        user.setStatus(rs.getString("status")); // ACTIVE, BANNED, PENDING
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setPhoneNumber(rs.getString("phone_number"));

        // is_first_login trong SQL là TINYINT(1), JDBC getBoolean tự hiểu
        user.setFirstLogin(rs.getBoolean("is_first_login"));

        return user;
    }

    // ==========================================
    // 2. CÁC HÀM TÌM KIẾM (READ)
    // ==========================================

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
        // Sửa: WHERE user_id = ?
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

    // ==========================================
    // 3. CHECK TỒN TẠI (VALIDATION)
    // ==========================================

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

    // ==========================================
    // 4. LƯU & CẬP NHẬT (WRITE)
    // ==========================================

    public boolean saveUser(User user) {
        // Sửa: INSERT vào cột "password" (không phải password_hash)
        // is_first_login mặc định là 1 (True)
        String sql = "INSERT INTO users (username, password, email, full_name, role, gender, status, is_first_login, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFullName());
            // Role & Gender lưu dưới dạng String để khớp với ENUM trong DB
            stmt.setString(5, user.getRole() != null ? user.getRole().name() : Role.USER.name());
            stmt.setString(6, user.getGender() != null ? user.getGender().name() : Gender.Other.name());
            stmt.setString(7, "ACTIVE"); // Giá trị mặc định ENUM
            stmt.setBoolean(8, true);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Lấy user_id tự sinh
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
        // Sửa: WHERE user_id = ?
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
        // Sửa: SET password = ? WHERE user_id = ?
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
        // Sửa: WHERE user_id = ?
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
        // Sửa: WHERE user_id = ?
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

    // ==========================================
    // 5. OTP & SECURITY
    // ==========================================

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

    // ==========================================
    // 6. XÓA TÀI KHOẢN (TRANSACTION)
    // ==========================================

    public boolean deleteUser(int userId) {
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Trong SQL của bạn đã có ON DELETE CASCADE, nên thực tế chỉ cần xóa users là đủ.
            // Tuy nhiên, để an toàn tuyệt đối và rõ ràng, ta vẫn có thể xóa bảng con trước hoặc để DB tự lo.
            // Ở đây tôi viết lệnh xóa trực tiếp User, DB sẽ tự động xóa history/favorites nhờ CASCADE.

            // Sửa: WHERE user_id = ?
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
}