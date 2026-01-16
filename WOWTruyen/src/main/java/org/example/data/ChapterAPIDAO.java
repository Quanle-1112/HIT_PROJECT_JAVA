//package org.example.data;
//
//import com.google.gson.Gson;
//import org.example.api.ApiGet;
//import org.example.api.apiAll.ApiChapterResponse;
//import org.example.model.chapter.ChapterInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChapterAPIDAO {
//    private final Gson gson = new Gson();
//
//    // Domain chứa ảnh (Có thể thay đổi tùy nguồn truyện)
//    private static final String IMAGE_DOMAIN = "https://sv1.otruyencdn.com";
//
//    /**
//     * Lấy danh sách đường dẫn ảnh của một chương
//     * @param chapterApiUrl Đường dẫn API chi tiết của chương (lấy từ ChapterInfo)
//     */
//    public List<String> getChapterImages(String chapterApiUrl) {
//        String json = ApiGet.getApi(chapterApiUrl);
//        List<String> imageUrls = new ArrayList<>();
//
//        if (json == null || json.isEmpty()) return imageUrls;
//
//        try {
//            ApiChapterResponse response = gson.fromJson(json, ApiChapterResponse.class);
//            if (response != null && response.getData() != null) {
//
//                String path = response.getData().getDomain_cdn();
//                // Lưu ý: API otruyen thường trả về domain_cdn + chapter_path + image_file
//                String chapterPath = response.getData().getItem().getChapterPath();
//
//                List<ApiChapterResponse.ChapterImage> images = response.getData().getItem().getChapterImages();
//
//                for (ApiChapterResponse.ChapterImage img : images) {
//                    // Ghép chuỗi để ra link ảnh hoàn chỉnh
//                    String fullUrl = String.format("%s/%s/%s", path, chapterPath, img.getImageFile());
//                    imageUrls.add(fullUrl);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return imageUrls;
//    }
//}