//package org.example.data;
//
//import org.example.api.apiAll.ApiBookItem;
//import org.example.api.apiAll.ApiChapterResponse;
//import org.example.model.book.Book;
//import org.example.model.book.BookCategory;
//import org.example.model.book.BookChapterLastest;
//import org.example.model.chapter.AllChapter;
//import org.example.model.chapter.ChapterInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DataMapper {
//
//    private static final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";
//
//    /**
//     * Chuyển đổi từ ApiBookItem (Dữ liệu thô từ Web) sang Book (Model dùng trong App)
//     */
//    public static Book toBookModel(ApiBookItem apiItem) {
//        if (apiItem == null) return null;
//
//        Book book = new Book();
//        book.setId(apiItem.getId());
//        book.setName(apiItem.getName());
//        book.setSlug(apiItem.getSlug());
//        book.setStatus(apiItem.getStatus());
//        book.setContent(apiItem.getContent());
//        book.setUpdatedAt(apiItem.getUpdatedAt());
//
//        // Xử lý ảnh thumbnail (Thêm Base URL nếu cần)
//        String thumb = apiItem.getThumbUrl();
//        if (thumb != null && !thumb.startsWith("http")) {
//            book.setThumbnail(IMAGE_BASE_URL + thumb);
//        } else {
//            book.setThumbnail(thumb);
//        }
//
//        // Map danh sách thể loại
//        if (apiItem.getCategory() != null) {
//            book.setCategory(apiItem.getCategory());
//        }
//
//        // Map chapter mới nhất
//        if (apiItem.getChaptersLatest() != null) {
//            book.setChapterLastests(apiItem.getChaptersLatest());
//        }
//
//        return book;
//    }
//
//    /**
//     * Chuyển đổi danh sách ApiBookItem sang danh sách Book
//     */
//    public static List<Book> toBookList(List<ApiBookItem> apiItems) {
//        List<Book> books = new ArrayList<>();
//        if (apiItems != null) {
//            for (ApiBookItem item : apiItems) {
//                books.add(toBookModel(item));
//            }
//        }
//        return books;
//    }
//}