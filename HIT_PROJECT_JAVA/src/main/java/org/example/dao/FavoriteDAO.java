package org.example.dao;

import org.example.model.user.UserFavorite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoriteDAO {

    public boolean isFavorite(int userId, String bookSlug) {
        String sql = "SELECT 1 FROM user_favorites WHERE user_id = ? AND book_slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, bookSlug);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addFavorite(UserFavorite fav) {
        String sql = "INSERT INTO user_favorites (user_id, book_slug, book_name, thumbnail_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fav.getUserId());
            stmt.setString(2, fav.getBookSlug());
            stmt.setString(3, fav.getBookName());
            stmt.setString(4, fav.getThumbnailUrl());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeFavorite(int userId, String bookSlug) {
        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND book_slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, bookSlug);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}