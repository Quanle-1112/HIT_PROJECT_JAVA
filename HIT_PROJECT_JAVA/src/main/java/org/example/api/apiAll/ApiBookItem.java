package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;
import org.example.model.book.BookCategory;
import org.example.model.book.BookChapterLastest;
import org.example.model.chapter.AllChapter;

import java.util.List;

public class ApiBookItem {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("origin_name")
    private List<String> originName;

    @SerializedName("content")
    private String content;

    @SerializedName("status")
    private String status;

    @SerializedName("thumb_url")
    private String thumbUrl;

    @SerializedName("sub_docquyen")
    private boolean subDocQuyen;

    @SerializedName("author")
    private List<String> author;

    @SerializedName("category")
    private List<BookCategory> category;

    @SerializedName("chapters")
    private List<AllChapter> chapters;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("chaptersLatest")
    private List<BookChapterLastest> chaptersLatest;

    public ApiBookItem() {
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

    public List<String> getOriginName() {
        return originName;
    }

    public void setOriginName(List<String> originName) {
        this.originName = originName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public boolean isSubDocQuyen() {
        return subDocQuyen;
    }

    public void setSubDocQuyen(boolean subDocQuyen) {
        this.subDocQuyen = subDocQuyen;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public List<BookCategory> getCategory() {
        return category;
    }

    public void setCategory(List<BookCategory> category) {
        this.category = category;
    }

    public List<AllChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<AllChapter> chapters) {
        this.chapters = chapters;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<BookChapterLastest> getChaptersLatest() {
        return chaptersLatest;
    }

    public void setChaptersLatest(List<BookChapterLastest> chaptersLatest) {
        this.chaptersLatest = chaptersLatest;
    }

    @Override
    public String toString() {
        return "ApiBookItem{" +
                "name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}