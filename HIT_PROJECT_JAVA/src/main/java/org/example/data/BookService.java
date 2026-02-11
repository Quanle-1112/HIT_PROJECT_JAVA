package org.example.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.api.ApiGet;
import org.example.api.apiAll.*;
import org.example.constant.MessageConstant;
import org.example.exception.NetworkException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class BookService {

    private final Gson gson = new Gson();
    private final String BASE_URL = "https://otruyenapi.com/v1/api";

    public List<ApiCategory> getAllCategories() {
        String url = BASE_URL + "/the-loai";
        String json = ApiGet.getApi(url);

        try {
            ApiCategoryResponse response = gson.fromJson(json, ApiCategoryResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItems();
            }
            return Collections.emptyList();
        } catch (JsonSyntaxException e) {
            throw new NetworkException(MessageConstant.ERR_LOAD_CATEGORIES, e);
        }
    }

    public List<ApiBookItem> getNewBooks(int page) {
        return fetchBooks(BASE_URL + "/danh-sach/truyen-moi?page=" + page);
    }

    public List<ApiBookItem> getCompletedBooks(int page) {
        return fetchBooks(BASE_URL + "/danh-sach/hoan-thanh?page=" + page);
    }

    public List<ApiBookItem> getComingSoonBooks(int page) {
        return fetchBooks(BASE_URL + "/danh-sach/sap-ra-mat?page=" + page);
    }

    public List<ApiBookItem> getBooksByCategory(String categorySlug, int page) {
        return fetchBooks(BASE_URL + "/the-loai/" + categorySlug + "?page=" + page);
    }

    private List<ApiBookItem> fetchBooks(String url) {
        String json = ApiGet.getApi(url);
        try {
            ApiAllBookResponse response = gson.fromJson(json, ApiAllBookResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItems();
            }
            return Collections.emptyList();
        } catch (JsonSyntaxException e) {
            throw new NetworkException(MessageConstant.ERR_LOAD_BOOKS, e);
        }
    }

    public ApiOneBookResponse.ApiOneBookData getBookDetail(String slug) {
        String url = BASE_URL + "/truyen-tranh/" + slug;
        String json = ApiGet.getApi(url);

        try {
            ApiOneBookResponse res = gson.fromJson(json, ApiOneBookResponse.class);
            if (res == null || res.getData() == null) {
                throw new NetworkException(MessageConstant.ERR_BOOK_NOT_FOUND);
            }
            return res.getData();
        } catch (JsonSyntaxException e) {
            throw new NetworkException(MessageConstant.ERR_API_FORMAT, e);
        }
    }

    public List<ApiBookItem> searchBooks(String query, int page) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "/tim-kiem?keyword=" + encoded + "&page=" + page;
            String json = ApiGet.getApi(url);

            ApiSearchBookResponse res = gson.fromJson(json, ApiSearchBookResponse.class);
            if (res != null && res.getData() != null) {
                return res.getData().getItems();
            }
            return Collections.emptyList();

        } catch (Exception e) {
            throw new NetworkException(MessageConstant.ERR_SEARCH, e);
        }
    }
}