package com.service.content.platform.model;

import java.beans.ConstructorProperties;

public class ContentMetaData {
    private final String contentId;
    private final String contentName;

    @ConstructorProperties({"contentId", "contentName"})
    public ContentMetaData(String contentId, String contentName) {
        this.contentId = contentId;
        this.contentName = contentName;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentName() {
        return contentName;
    }

}
