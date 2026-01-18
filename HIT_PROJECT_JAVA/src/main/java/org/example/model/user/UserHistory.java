package org.example.model.user;


import java.sql.Timestamp;

public class UserHistory {
    private int historyId;
    private int userId;
    private String bookSlug;
    private String bookName;
    private String thumbnailUrl;
    private String lastChapterName;
    private String lastChapterApiData;
    private Timestamp readAt;

    public UserHistory() {
    }

    public UserHistory(int userId, String bookSlug, String bookName, String thumbnailUrl, String lastChapterName, String lastChapterApiData) {
        this.userId = userId;
        this.bookSlug = bookSlug;
        this.bookName = bookName;
        this.thumbnailUrl = thumbnailUrl;
        this.lastChapterName = lastChapterName;
        this.lastChapterApiData = lastChapterApiData;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getBookSlug() { return bookSlug; }
    public void setBookSlug(String bookSlug) { this.bookSlug = bookSlug; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getLastChapterName() { return lastChapterName; }
    public void setLastChapterName(String lastChapterName) { this.lastChapterName = lastChapterName; }

    public String getLastChapterApiData() { return lastChapterApiData; }
    public void setLastChapterApiData(String lastChapterApiData) { this.lastChapterApiData = lastChapterApiData; }

    public Timestamp getReadAt() { return readAt; }
    public void setReadAt(Timestamp readAt) { this.readAt = readAt; }
}