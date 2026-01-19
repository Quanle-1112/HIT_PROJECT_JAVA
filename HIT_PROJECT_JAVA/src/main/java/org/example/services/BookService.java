package org.example.services;

import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiOneBookResponse;
import java.util.List;

public interface BookService {
    List<ApiBookItem> getNewBooks(int page);

    List<ApiBookItem> searchBooks(String keyword, int page);

    ApiOneBookResponse.ApiOneBookData getBookDetail(String slug);
}