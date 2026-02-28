package org.example.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.example.constant.MessageConstant;
import org.example.exception.NetworkException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiGet {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    public static String getApi(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new NetworkException(MessageConstant.ERR_NETWORK + " (Code: " + response.code() + ")");
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new NetworkException(MessageConstant.ERR_API_EMPTY);
            }

            return body.string();

        } catch (IOException e) {
            throw new NetworkException(MessageConstant.ERR_NETWORK, e);
        }
    }
}