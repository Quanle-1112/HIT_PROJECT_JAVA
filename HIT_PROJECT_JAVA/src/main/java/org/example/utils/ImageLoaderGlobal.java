package org.example.utils;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageLoaderGlobal {

    private static final Map<String, Image> imageCache = Collections.synchronizedMap(new HashMap<>());

    private static final ExecutorService executor = Executors.newFixedThreadPool(50);

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    public static void setImage(String url, ImageView imageView) {
        if (url == null || url.isEmpty()) return;

        if (imageCache.containsKey(url)) {
            imageView.setImage(imageCache.get(url));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        byte[] imageBytes = response.body().bytes();

                        Image image = new Image(new ByteArrayInputStream(imageBytes));

                        imageCache.put(url, image);

                        Platform.runLater(() -> {
                            imageView.setImage(image);
                            fadeIn(imageView);
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi tải ảnh: " + url);
            }
        }, executor);
    }

    private static void fadeIn(ImageView node) {
        node.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}