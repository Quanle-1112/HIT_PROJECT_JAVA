package org.example.model.book;


import org.example.model.chapter.AllChapter;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String id;
    private String name;
    private String slug;
    private String status;
    private String updatedAt;
    private String thumbnail;
    private String content;
    private List<BookCategory> category;
    private List<BookChapterLastest> chapterLastests;
    private List<AllChapter> chapter;

    public Book() {
        this.category = new ArrayList<>();
        this.chapterLastests = new ArrayList<>();
        this.chapter = new ArrayList<>();
    }


    public Book(String name, String slug, String thumbnail) {
        this.name = name;
        this.slug = slug;
        this.thumbnail = thumbnail;
    }

    public Book(String id, String name, String slug, String status, String updatedAt, String thumbnail, List<BookCategory> category, List<BookChapterLastest> chapterLastests) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.status = status;
        this.updatedAt = updatedAt;
        this.thumbnail = thumbnail;
        this.category = category;
        this.chapterLastests = chapterLastests;
    }

    public Book(String id, String name, String slug, String content, String status, String thumbnail, List<BookCategory> category, List<AllChapter> chapter, String updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.content = content;
        this.status = status;
        this.thumbnail = thumbnail;
        this.category = category;
        this.chapter = chapter;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<BookCategory> getCategory() {
        return category;
    }

    public void setCategory(List<BookCategory> category) {
        this.category = category;
    }

    public List<BookChapterLastest> getChapterLastests() {
        return chapterLastests;
    }

    public void setChapterLastests(List<BookChapterLastest> chapterLastests) {
        this.chapterLastests = chapterLastests;
    }

    public List<AllChapter> getChapters() {
        return chapter;
    }

    public void setChapters(List<AllChapter> chapters) {
        this.chapter = chapters;
    }


    public String getCategoriesAsString() {
        if (category == null || category.isEmpty()) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (BookCategory c : category) {
            sb.append(c.getName()).append(", ");
        }
        if (sb.length() > 2) {
            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }
}