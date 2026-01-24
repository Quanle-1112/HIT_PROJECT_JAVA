package org.example.data;

import com.google.gson.Gson;
import org.example.api.ApiGet;
import org.example.api.apiAll.ApiAllBookResponse;
import org.example.api.apiAll.ApiBookItem;
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

    private List<ApiBookItem> fetchBooksFromUrl(String url) {
        String jsonResponse = ApiGet.getApi(url);
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ApiAllBookResponse response = gson.fromJson(jsonResponse, ApiAllBookResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public List<ApiBookItem> getBooksByCategory(String categorySlug, int page) {
        return fetchBooksFromUrl(BASE_URL + "/the-loai/" + categorySlug + "?page=" + page);
    }

    public List<ApiBookItem> searchBooks(String keyword, int page) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = BASE_URL + "/tim-kiem?keyword=" + encodedKeyword + "&page=" + page;

            String jsonResponse = ApiGet.getApi(url);
            if (jsonResponse == null) return Collections.emptyList();

            ApiSearchBookResponse response = gson.fromJson(jsonResponse, ApiSearchBookResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public ApiOneBookResponse.ApiOneBookData getBookDetail(String slug) {
        String url = BASE_URL + "/truyen-tranh/" + slug;
        String jsonResponse = ApiGet.getApi(url);
        if (jsonResponse == null) return null;
        try {
            ApiOneBookResponse response = gson.fromJson(jsonResponse, ApiOneBookResponse.class);
            if (response != null) return response.getData();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}