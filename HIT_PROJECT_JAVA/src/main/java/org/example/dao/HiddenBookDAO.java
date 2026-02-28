package org.example.dao;

import org.example.api.apiAll.ApiBookItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HiddenBookDAO {

    public boolean hideBook(String slug, String name, String thumbUrl) {
        String sql = "INSERT INTO hidden_books (slug, name, thumb_url) VALUES (?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            stmt.setString(2, name);
            stmt.setString(3, thumbUrl);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean showBook(String slug) {
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

    public boolean isHidden(String slug) {
        String sql = "SELECT 1 FROM hidden_books WHERE slug = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<String> getAllHiddenSlugs() {
        Set<String> slugs = new HashSet<>();
        String sql = "SELECT slug FROM hidden_books";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                slugs.add(rs.getString("slug"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slugs;
    }

    public List<ApiBookItem> getAllHiddenBooksForAdmin() {
        List<ApiBookItem> list = new ArrayList<>();
        String sql = "SELECT slug, name, thumb_url FROM hidden_books";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ApiBookItem book = new ApiBookItem();
                book.setSlug(rs.getString("slug"));
                book.setName(rs.getString("name"));
                book.setThumbUrl(rs.getString("thumb_url"));
                list.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}