package org.example.model.user;


import java.sql.Timestamp;

public class UserFavorite {
    private int favoriteId;
    private int userId;
    private String bookSlug;
    private String bookName;
    private String thumbnailUrl;
    private Timestamp addedAt;

    public UserFavorite() {
    }

    public UserFavorite(int userId, String bookSlug, String bookName, String thumbnailUrl) {
        this.userId = userId;
        this.bookSlug = bookSlug;
        this.bookName = bookName;
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getFavoriteId() { return favoriteId; }
    public void setFavoriteId(int favoriteId) { this.favoriteId = favoriteId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getBookSlug() { return bookSlug; }
    public void setBookSlug(String bookSlug) { this.bookSlug = bookSlug; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
}