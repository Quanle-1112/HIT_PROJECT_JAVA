package org.example.model.book;

public class BookChapterLastest {
    private String filename;
    private String chapter_name;
    private String chapter_api_data;

    public BookChapterLastest() {
    }

    public BookChapterLastest(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_api_data() {
        return chapter_api_data;
    }

    public void setChapter_api_data(String chapter_api_data) {
        this.chapter_api_data = chapter_api_data;
    }
}