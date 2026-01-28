package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;
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

    @SerializedName("status")
    private String status;

    @SerializedName("thumb_url")
    private String thumbUrl;

    @SerializedName("sub_docquyen")
    private boolean subDocquyen;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("chaptersLatest")
    private List<BookChapterLastest> chaptersLatest;

    @SerializedName("chapters")
    private List<AllChapter> chapters;

    @SerializedName("category")
    private List<ApiCategory> category;


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

    public boolean isSubDocquyen() {
        return subDocquyen;
    }

    public void setSubDocquyen(boolean subDocquyen) {
        this.subDocquyen = subDocquyen;
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

    public List<AllChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<AllChapter> chapters) {
        this.chapters = chapters;
    }

    public List<ApiCategory> getCategory() {
        return category;
    }

    public void setCategory(List<ApiCategory> category) {
        this.category = category;
    }
}