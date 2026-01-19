package org.example.services.impl;

import com.google.gson.Gson;
import org.example.api.ApiGet;
import org.example.api.apiAll.ApiChapterResponse;
import org.example.services.ChapterService;

public class ChapterServiceImpl implements ChapterService {
    private final Gson gson = new Gson();

    @Override
    public ApiChapterResponse.ChapterItem getChapterContent(String chapterApiDataUrl) {
        String json = ApiGet.getApi(chapterApiDataUrl);

        if (json == null) return null;

        try {
            ApiChapterResponse response = gson.fromJson(json, ApiChapterResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItem();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}