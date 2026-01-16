//package org.example.data;
//
//import com.google.gson.Gson;
//import org.example.api.ApiGet;
//import org.example.api.apiAll.ApiAllBookResponse;
//import org.example.api.apiAll.ApiOneBookResponse;
//import org.example.api.apiAll.ApiSearchBookResponse;
//import org.example.model.book.Book;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BookAPIDAO {
//    private final Gson gson = new Gson();
//
//    // Các Endpoint API
//    private static final String BASE_API = "https://otruyenapi.com/v1/api/";
//    private static final String HOME_NEW = BASE_API + "danh-sach/truyen-moi?page=";
//    private static final String HOME_COMING = BASE_API + "danh-sach/sap-ra-mat?page=";
//    private static final String HOME_COMPLETED = BASE_API + "danh-sach/hoan-thanh?page=";
//    private static final String SEARCH_URL = BASE_API + "tim-kiem?keyword=";
//    private static final String DETAIL_URL = BASE_API + "truyen-tranh/";
//
//    /**
//     * Lấy danh sách truyện mới cập nhật
//     */
//    public List<Book> getNewBooks(int page) {
//        return fetchListBook(HOME_NEW + page);
//    }
//
//    /**
//     * Lấy danh sách truyện sắp ra mắt
//     */
//    public List<Book> getComingSoonBooks(int page) {
//        return fetchListBook(HOME_COMING + page);
//    }
//
//    /**
//     * Lấy danh sách truyện đã hoàn thành
//     */
//    public List<Book> getCompletedBooks(int page) {
//        return fetchListBook(HOME_COMPLETED + page);
//    }
//
//    /**
//     * Tìm kiếm truyện theo từ khóa
//     */
//    public List<Book> searchBooks(String keyword) {
//        // Cần encode URL nếu từ khóa có dấu cách hoặc tiếng Việt (đã xử lý đơn giản ở đây)
//        String url = SEARCH_URL + keyword.replace(" ", "%20");
//        String json = ApiGet.getApi(url);
//
//        if (json == null || json.isEmpty()) return new ArrayList<>();
//
//        try {
//            ApiSearchBookResponse response = gson.fromJson(json, ApiSearchBookResponse.class);
//            if (response != null && response.getData() != null) {
//                return DataMapper.toBookList(response.getData().getItems());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * Lấy chi tiết 1 cuốn truyện (Bao gồm nội dung, danh sách chương)
//     */
//    public Book getBookDetails(String slug) {
//        String url = DETAIL_URL + slug;
//        String json = ApiGet.getApi(url);
//
//        if (json == null || json.isEmpty()) return null;
//
//        try {
//            ApiOneBookResponse response = gson.fromJson(json, ApiOneBookResponse.class);
//            if (response != null && response.getData() != null) {
//                Book book = DataMapper.toBookModel(response.getData().getItem());
//                // Gán thêm danh sách chương đầy đủ vào Book
//                book.setChapters(response.getData().getItem().getChapters());
//                return book;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // --- Private Helper ---
//    private List<Book> fetchListBook(String url) {
//        String json = ApiGet.getApi(url);
//        if (json == null || json.isEmpty()) return new ArrayList<>();
//
//        try {
//            ApiAllBookResponse response = gson.fromJson(json, ApiAllBookResponse.class);
//            if (response != null && response.getData() != null) {
//                return DataMapper.toBookList(response.getData().getItems());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }
//}