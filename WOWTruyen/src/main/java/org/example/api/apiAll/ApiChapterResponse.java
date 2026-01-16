package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiChapterResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiChapterData data;

    // --- Getters & Setters cho ApiChapterResponse ---
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

    // ==========================================
    // INNER CLASS: ApiChapterData
    // ==========================================
    public static class ApiChapterData {
        @SerializedName("item")
        private ChapterItem item;

        // --- Getters & Setters ---
        public ChapterItem getItem() {
            return item;
        }

        public void setItem(ChapterItem item) {
            this.item = item;
        }
    }

    // ==========================================
    // INNER CLASS: ChapterItem (Chứa thông tin chương và đường dẫn ảnh)
    // ==========================================
    public static class ChapterItem {
        @SerializedName("_id")
        private String id;

        @SerializedName("chapter_name")
        private String chapterName;

        @SerializedName("chapter_path")
        private String chapterPath; // Đường dẫn gốc (VD: uploads/manga/abc...)

        @SerializedName("chapter_image")
        private List<ChapterImage> chapterImages; // Danh sách các file ảnh

        // --- Getters & Setters ---
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

    // ==========================================
    // INNER CLASS: ChapterImage (Tên file ảnh cụ thể)
    // ==========================================
    public static class ChapterImage {
        @SerializedName("image_file")
        private String imageFile;

        @SerializedName("page")
        private int page;

        // --- Getters & Setters ---
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