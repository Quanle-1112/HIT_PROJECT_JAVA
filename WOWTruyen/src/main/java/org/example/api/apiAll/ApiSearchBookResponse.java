package org.example.api.apiAll;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiSearchBookResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiSearchData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApiSearchData getData() {
        return data;
    }

    public void setData(ApiSearchData data) {
        this.data = data;
    }

    // --- Inner Classes ---

    public static class ApiSearchData {
        @SerializedName("items")
        private List<ApiBookItem> items;

        @SerializedName("titlePage")
        private String titlePage;

        @SerializedName("breadCrumb")
        private List<BreadCrumb> breadCrumb;

        public List<ApiBookItem> getItems() {
            return items;
        }

        public void setItems(List<ApiBookItem> items) {
            this.items = items;
        }

        public String getTitlePage() {
            return titlePage;
        }

        public void setTitlePage(String titlePage) {
            this.titlePage = titlePage;
        }

        public List<BreadCrumb> getBreadCrumb() {
            return breadCrumb;
        }

        public void setBreadCrumb(List<BreadCrumb> breadCrumb) {
            this.breadCrumb = breadCrumb;
        }
    }

    public static class BreadCrumb {
        @SerializedName("name")
        private String name;
        @SerializedName("slug")
        private String slug;

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
    }
}