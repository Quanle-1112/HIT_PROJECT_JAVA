package org.example.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AdminDAO {

    public boolean hideBook(String slug, String name) {
        String sql = "INSERT INTO hidden_books (slug, name, hidden_at) VALUES (?, ?, NOW()) ON DUPLICATE KEY UPDATE hidden_at = NOW()";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            stmt.setString(2, name);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unhideBook(String slug) {
        String sql = "DELETE FROM hidden_books WHERE slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<String> getHiddenBookSlugs() {
        Set<String> hiddenSlugs = new HashSet<>();
        String sql = "SELECT slug FROM hidden_books";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                hiddenSlugs.add(rs.getString("slug"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hiddenSlugs;
    }

    public int countTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countHiddenBooks() {
        String sql = "SELECT COUNT(*) FROM hidden_books";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}