package org.example.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.api.ApiGet;
import org.example.api.apiAll.ApiChapterResponse;
import org.example.constant.MessageConstant;
import org.example.exception.NetworkException;

public class ChapterService {
    private final Gson gson = new Gson();

    public ApiChapterResponse.ChapterItem getChapterContent(String chapterApiDataUrl) {
        String json = ApiGet.getApi(chapterApiDataUrl);

        try {
            ApiChapterResponse response = gson.fromJson(json, ApiChapterResponse.class);
            if (response != null && response.getData() != null) {
                return response.getData().getItem();
            }
            throw new NetworkException(MessageConstant.ERR_CHAPTER_CONTENT);

        } catch (JsonSyntaxException e) {
            throw new NetworkException(MessageConstant.ERR_API_FORMAT, e);
        } catch (Exception e) {
            throw new NetworkException(MessageConstant.ERR_SYSTEM, e);
        }
    }
}