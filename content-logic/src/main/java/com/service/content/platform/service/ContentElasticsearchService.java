package com.service.content.platform.service;

import com.service.content.platform.model.Content;

import java.io.IOException;
import java.util.List;

/**
 * Service for managing contents elasticsearch.
 */
public interface ContentElasticsearchService {

    String createContent(Content content) throws IOException;

    String updateContent(Content content) throws IOException;

    String deleteContent(String id) throws IOException;

    Content findById(String id) throws IOException;

    List<Content> findContentsByName(String name) throws IOException;

    List<Content> findAll() throws IOException;

}
