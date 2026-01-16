package org.example.api.apiAll;


import com.google.gson.annotations.SerializedName;

public class ApiOneBookResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiOneBookData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApiOneBookData getData() {
        return data;
    }

    public void setData(ApiOneBookData data) {
        this.data = data;
    }

    // --- Inner Classes ---

    public static class ApiOneBookData {
        @SerializedName("item")
        private ApiBookItem item; // Đối tượng truyện chi tiết

        @SerializedName("seoOnPage")
        private SeoOnPage seoOnPage;

        public ApiBookItem getItem() {
            return item;
        }

        public void setItem(ApiBookItem item) {
            this.item = item;
        }

        public SeoOnPage getSeoOnPage() {
            return seoOnPage;
        }

        public void setSeoOnPage(SeoOnPage seoOnPage) {
            this.seoOnPage = seoOnPage;
        }
    }

    public static class SeoOnPage {
        @SerializedName("titleHead")
        private String titleHead;
        @SerializedName("descriptionHead")
        private String descriptionHead;

        public String getTitleHead() {
            return titleHead;
        }

        public void setTitleHead(String titleHead) {
            this.titleHead = titleHead;
        }

        public String getDescriptionHead() {
            return descriptionHead;
        }

        public void setDescriptionHead(String descriptionHead) {
            this.descriptionHead = descriptionHead;
        }
    }
}