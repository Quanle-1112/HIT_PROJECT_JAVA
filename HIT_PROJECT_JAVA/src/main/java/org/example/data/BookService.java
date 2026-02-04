package org.example.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.example.api.ApiGet;
import org.example.api.apiAll.ApiAllBookResponse;
import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiCategory;
import org.example.api.apiAll.ApiOneBookResponse;
import org.example.api.apiAll.ApiSearchBookResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookService {

    private final Gson gson = new Gson();
    private final String BASE_URL = "https://otruyenapi.com/v1/api";
    private final int ITEM_LIMIT = 24;

    public List<ApiCategory> getAllCategories() {
        String url = "https://otruyenapi.com/v1/api/the-loai";
        System.out.println("Đang gọi API: " + url);

        String jsonResponse = ApiGet.getApi(url);

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            System.err.println("API trả về rỗng!");
            return Collections.emptyList();
        }

        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);
            if (root.has("data")) {
                JsonObject data = root.getAsJsonObject("data");
                if (data.has("items")) {
                    JsonArray items = data.getAsJsonArray("items");

                    List<ApiCategory> categories = new ArrayList<>();
                    for (JsonElement elem : items) {
                        ApiCategory cat = gson.fromJson(elem, ApiCategory.class);
                        categories.add(cat);
                    }
                    System.out.println("Đã tìm thấy " + categories.size() + " thể loại.");
                    return categories;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi phân tích JSON thể loại: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<ApiBookItem> fetchBooksFromUrl(String url) {
        String jsonResponse = ApiGet.getApi(url);
        if (jsonResponse == null || jsonResponse.isEmpty()) return Collections.emptyList();
        try {
            ApiAllBookResponse response = gson.fromJson(jsonResponse, ApiAllBookResponse.class);
            if (response != null && response.getData() != null) return response.getData().getItems();
        } catch (Exception e) { e.printStackTrace(); }
        return Collections.emptyList();
    }

    public List<ApiBookItem> getNewBooks(int page) {
        return fetchBooksFromUrl(BASE_URL + "/danh-sach/truyen-moi?page=" + page);
    }
    public List<ApiBookItem> getCompletedBooks(int page) {
        return fetchBooksFromUrl(BASE_URL + "/danh-sach/hoan-thanh?page=" + page);
    }
    public List<ApiBookItem> getComingSoonBooks(int page) {
        return fetchBooksFromUrl(BASE_URL + "/danh-sach/sap-ra-mat?page=" + page);
    }

    public ApiOneBookResponse.ApiOneBookData getBookDetail(String slug) {
        String url = BASE_URL + "/truyen-tranh/" + slug;
        String json = ApiGet.getApi(url);
        if (json == null) return null;
        try {
            ApiOneBookResponse res = gson.fromJson(json, ApiOneBookResponse.class);
            return (res != null) ? res.getData() : null;
        } catch (Exception e) { return null; }
    }

    public List<ApiBookItem> searchBooks(String query, int page) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "/tim-kiem?keyword=" + encoded + "&page=" + page;
            String json = ApiGet.getApi(url);
            if (json == null) return Collections.emptyList();
            ApiSearchBookResponse res = gson.fromJson(json, ApiSearchBookResponse.class);
            return (res != null && res.getData() != null) ? res.getData().getItems() : Collections.emptyList();
        } catch (Exception e) { return Collections.emptyList(); }
    }

    public List<ApiBookItem> getBooksByCategory(String slug, int page) {
        return fetchBooksFromUrl(BASE_URL + "/the-loai/" + slug + "?page=" + page);
    }
}