package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiChapterResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiChapterData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApiChapterData getData() {
        return data;
    }

    public void setData(ApiChapterData data) {
        this.data = data;
    }

    public static class ApiChapterData {
        @SerializedName("item")
        private ChapterItem item;

        public ChapterItem getItem() {
            return item;
        }

        public void setItem(ChapterItem item) {
            this.item = item;
        }
    }

    public static class ChapterItem {
        @SerializedName("_id")
        private String id;

        @SerializedName("chapter_name")
        private String chapterName;

        @SerializedName("chapter_path")
        private String chapterPath;

        @SerializedName("chapter_image")
        private List<ChapterImage> chapterImages;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getChapterPath() {
            return chapterPath;
        }

        public void setChapterPath(String chapterPath) {
            this.chapterPath = chapterPath;
        }

        public List<ChapterImage> getChapterImages() {
            return chapterImages;
        }

        public void setChapterImages(List<ChapterImage> chapterImages) {
            this.chapterImages = chapterImages;
        }
    }

    public static class ChapterImage {
        @SerializedName("image_file")
        private String imageFile;

        @SerializedName("page")
        private int page;

        public String getImageFile() {
            return imageFile;
        }

        public void setImageFile(String imageFile) {
            this.imageFile = imageFile;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}