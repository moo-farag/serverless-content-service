package com.service.content.trigger.stream;

import java.beans.ConstructorProperties;
import java.util.UUID;


public class ContentMessage {

    private UUID id;

    private String contentId;

    private String contentName;

    private MessageOperation operation;

    private String originator;

    @ConstructorProperties({})
    public ContentMessage() {
    }

    @ConstructorProperties({"contentId", "contentName", "operation", "originator"})
    public ContentMessage(String contentId, String contentName, MessageOperation operation, String originator) {
        this.contentId = contentId;
        this.contentName = contentName;
        this.operation = operation;
        this.originator = originator;
    }

    @ConstructorProperties({"contentId", "operation", "originator"})
    public ContentMessage(String contentId, MessageOperation operation, String originator) {
        this.contentId = contentId;
        this.operation = operation;
        this.originator = originator;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public MessageOperation getOperation() {
        return operation;
    }

    public void setOperation(MessageOperation operation) {
        this.operation = operation;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

}
