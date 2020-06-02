package com.service.content.platform.service;

import com.service.content.platform.model.Content;

import java.io.IOException;
import java.util.List;

/**
 * Service for managing contents.
 */
public interface ContentService {


    List<Content> getAll() throws IOException;

    Content createContent(Content content)  throws IOException;

    Content updateContent(Content content)  throws IOException;

    Content findById(String contentId) throws IOException;

    void delete(String contentId) throws IOException;

    List<Content> searchByName(String query) throws IOException;
}
