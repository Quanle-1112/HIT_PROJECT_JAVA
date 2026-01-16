package org.example.model.chapter;


import com.google.gson.annotations.SerializedName;

public class ChapterInfo {
    @SerializedName("filename")
    private String filename;

    @SerializedName("chapter_name")
    private String chapterName;

    @SerializedName("chapter_api_data")
    private String chapterApiData;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterApiData() {
        return chapterApiData;
    }

    public void setChapterApiData(String chapterApiData) {
        this.chapterApiData = chapterApiData;
    }

    @Override
    public String toString() {
        return chapterName;
    }
}