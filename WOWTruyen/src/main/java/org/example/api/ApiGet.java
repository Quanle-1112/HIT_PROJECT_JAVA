package org.example.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiGet {

    // 1. Tạo Client Singleton: Giữ kết nối mở để tái sử dụng
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Chờ kết nối 30s
            .readTimeout(30, TimeUnit.SECONDS)    // Chờ tải về 30s
            .writeTimeout(30, TimeUnit.SECONDS)   // Chờ gửi đi 30s
            .retryOnConnectionFailure(true)       // Tự thử lại nếu rớt mạng
            .build();

    /**
     * Phương thức gọi API (GET Request)
     * @param url Link API cần gọi
     * @return Dữ liệu JSON (String) hoặc null nếu lỗi
     */
    public static String getApi(String url) {
        // Tạo yêu cầu
        Request request = new Request.Builder()
                .url(url)
                .get() // Mặc định là GET
                .addHeader("User-Agent", "Mozilla/5.0") // Giả lập trình duyệt
                .build();

        // Thực thi
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Lỗi API [" + response.code() + "]: " + url);
                return null;
            }

            ResponseBody body = response.body();
            return body != null ? body.string() : "";

        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}