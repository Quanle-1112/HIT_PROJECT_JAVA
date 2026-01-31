package org.example.dao;

import org.example.model.user.UserHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    public boolean saveHistory(UserHistory history) {
        String sql = "INSERT INTO user_history (user_id, book_slug, book_name, thumbnail_url, last_chapter_name, last_chapter_api_data, read_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "last_chapter_name = VALUES(last_chapter_name), " +
                "last_chapter_api_data = VALUES(last_chapter_api_data), " +
                "read_at = NOW()";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, history.getUserId());
            stmt.setString(2, history.getBookSlug());
            stmt.setString(3, history.getBookName());
            stmt.setString(4, history.getThumbnailUrl());
            stmt.setString(5, history.getLastChapterName());
            stmt.setString(6, history.getLastChapterApiData());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserHistory> getHistoryByUserId(int userId) {
        List<UserHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM user_history WHERE user_id = ? ORDER BY read_at DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserHistory history = new UserHistory();
                history.setHistoryId(rs.getInt("history_id"));
                history.setUserId(rs.getInt("user_id"));
                history.setBookSlug(rs.getString("book_slug"));
                history.setBookName(rs.getString("book_name"));
                history.setThumbnailUrl(rs.getString("thumbnail_url"));
                history.setLastChapterName(rs.getString("last_chapter_name"));
                history.setLastChapterApiData(rs.getString("last_chapter_api_data"));
                history.setReadAt(rs.getTimestamp("read_at"));
                list.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}