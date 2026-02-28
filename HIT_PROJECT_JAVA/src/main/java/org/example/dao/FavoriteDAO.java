package org.example.dao;

import org.example.constant.MessageConstant;
import org.example.exception.DatabaseException;
import org.example.model.user.UserFavorite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDAO {

    public boolean isFavorite(int userId, String bookSlug) {
        String sql = "SELECT 1 FROM user_favorites WHERE user_id = ? AND book_slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, bookSlug);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
    }

    public boolean addFavorite(UserFavorite fav) {
        String sql = "INSERT IGNORE INTO user_favorites (user_id, book_slug, book_name, thumbnail_url, added_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fav.getUserId());
            stmt.setString(2, fav.getBookSlug());
            stmt.setString(3, fav.getBookName());
            stmt.setString(4, fav.getThumbnailUrl());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_SAVE, e);
        }
    }

    public boolean removeFavorite(int userId, String bookSlug) {
        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND book_slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, bookSlug);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_DELETE, e);
        }
    }

    public List<UserFavorite> getFavoritesByUserId(int userId) {
        List<UserFavorite> list = new ArrayList<>();
        String sql = "SELECT * FROM user_favorites WHERE user_id = ? ORDER BY added_at DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserFavorite fav = new UserFavorite();
                fav.setFavoriteId(rs.getInt("favorite_id"));
                fav.setUserId(rs.getInt("user_id"));
                fav.setBookSlug(rs.getString("book_slug"));
                fav.setBookName(rs.getString("book_name"));
                fav.setThumbnailUrl(rs.getString("thumbnail_url"));
                fav.setAddedAt(rs.getTimestamp("added_at"));
                list.add(fav);
            }
        } catch (SQLException e) {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, e);
        }
        return list;
    }
}