package org.example.services.data;

import org.example.api.apiAll.ApiChapterResponse;

public interface ChapterService {
    ApiChapterResponse.ChapterItem getChapterContent(String chapterApiDataUrl);
}