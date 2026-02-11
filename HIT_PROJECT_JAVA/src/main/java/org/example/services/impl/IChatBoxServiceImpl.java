package org.example.services.impl;

import okhttp3.*;
import org.example.constant.MessageConstant;
import org.example.exception.NetworkException;
import org.example.services.IChatBoxService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class IChatBoxServiceImpl implements IChatBoxService {

    private final OkHttpClient client;

    public IChatBoxServiceImpl() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String askGemini(String prompt) {
        String url = MessageConstant.GEMINI_API_URL + MessageConstant.GEMINI_API_KEY;

        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONObject parts = new JSONObject();
        parts.put("parts", new JSONArray().put(textPart));

        JSONObject jsonBodyObj = new JSONObject();
        jsonBodyObj.put("contents", new JSONArray().put(parts));

        RequestBody body = RequestBody.create(jsonBodyObj.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new NetworkException(MessageConstant.CHAT_ERR_API + response.code());
            }

            if (response.body() == null) {
                throw new NetworkException(MessageConstant.ERR_API_EMPTY);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            JSONArray candidates = jsonResponse.optJSONArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new NetworkException(MessageConstant.CHAT_ERR_NO_RESPONSE);
            }

            return candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (IOException e) {
            throw new NetworkException(MessageConstant.ERR_NETWORK, e);
        } catch (Exception e) {
            throw new NetworkException(MessageConstant.ERR_SYSTEM, e);
        }
    }
}