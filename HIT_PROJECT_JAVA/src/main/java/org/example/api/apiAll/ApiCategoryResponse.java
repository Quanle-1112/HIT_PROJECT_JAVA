package org.example.api.apiAll;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiCategoryResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private ApiCategoryData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApiCategoryData getData() {
        return data;
    }

    public void setData(ApiCategoryData data) {
        this.data = data;
    }

    public static class ApiCategoryData {

        @SerializedName("items")
        private List<ApiCategory> items;

        @SerializedName("titlePage")
        private String titlePage;

        @SerializedName("type_list")
        private String typeList;

        public List<ApiCategory> getItems() {
            return items;
        }

        public void setItems(List<ApiCategory> items) {
            this.items = items;
        }

        public String getTitlePage() {
            return titlePage;
        }

        public void setTitlePage(String titlePage) {
            this.titlePage = titlePage;
        }

        public String getTypeList() {
            return typeList;
        }

        public void setTypeList(String typeList) {
            this.typeList = typeList;
        }
    }
}