package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiAllBookResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiAllBookData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApiAllBookData getData() {
        return data;
    }

    public void setData(ApiAllBookData data) {
        this.data = data;
    }


    public static class ApiAllBookData {
        @SerializedName("items")
        private List<ApiBookItem> items;

        @SerializedName("params")
        private PaginationParams params;

        @SerializedName("type_list")
        private String typeList;

        @SerializedName("titlePage")
        private String titlePage;

        public List<ApiBookItem> getItems() {
            return items;
        }

        public void setItems(List<ApiBookItem> items) {
            this.items = items;
        }

        public PaginationParams getParams() {
            return params;
        }

        public void setParams(PaginationParams params) {
            this.params = params;
        }

        public String getTypeList() {
            return typeList;
        }

        public void setTypeList(String typeList) {
            this.typeList = typeList;
        }

        public String getTitlePage() {
            return titlePage;
        }

        public void setTitlePage(String titlePage) {
            this.titlePage = titlePage;
        }
    }

    public static class PaginationParams {
        @SerializedName("type_slug")
        private String typeSlug;

        @SerializedName("filterCategory")
        private List<String> filterCategory;

        @SerializedName("sortField")
        private String sortField;

        @SerializedName("pagination")
        private PaginationDetail pagination;

        public String getTypeSlug() {
            return typeSlug;
        }

        public void setTypeSlug(String typeSlug) {
            this.typeSlug = typeSlug;
        }

        public List<String> getFilterCategory() {
            return filterCategory;
        }

        public void setFilterCategory(List<String> filterCategory) {
            this.filterCategory = filterCategory;
        }

        public String getSortField() {
            return sortField;
        }

        public void setSortField(String sortField) {
            this.sortField = sortField;
        }

        public PaginationDetail getPagination() {
            return pagination;
        }

        public void setPagination(PaginationDetail pagination) {
            this.pagination = pagination;
        }
    }

    public static class PaginationDetail {
        @SerializedName("totalItems")
        private int totalItems;
        @SerializedName("totalItemsPerPage")
        private int totalItemsPerPage;
        @SerializedName("currentPage")
        private int currentPage;
        @SerializedName("totalPages")
        private int totalPages;

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalItemsPerPage() {
            return totalItemsPerPage;
        }

        public void setTotalItemsPerPage(int totalItemsPerPage) {
            this.totalItemsPerPage = totalItemsPerPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}