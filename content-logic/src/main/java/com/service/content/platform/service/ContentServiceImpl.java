package com.service.content.platform.service;

import com.service.content.platform.model.Content;
import com.service.content.platform.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.List;

/**
 * Service Implementation class for managing contents.
 */
@Slf4j
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentElasticsearchService contentElasticsearchService;

    @Autowired
    @ConstructorProperties({"contentRepository", "contentElasticsearchService"})
    public ContentServiceImpl(ContentRepository contentRepository, ContentElasticsearchService contentElasticsearchService) {
        this.contentRepository = contentRepository;
        this.contentElasticsearchService = contentElasticsearchService;
    }

    /**
     * Get all contents
     *
     * @return a list of contents
     */
    @Override
    @Transactional(readOnly = true)
    public List<Content> getAll() throws IOException {
        return contentElasticsearchService.findAll();
    }

    /**
     * Create content
     *
     * @param content
     * @return a content
     */
    @Override
    public Content createContent(Content content) throws IOException {
        Content savedContent = contentRepository.save(content);
        log.debug("Created Content: {}", savedContent);
        try {
            contentElasticsearchService.createContent(savedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedContent;
    }

    /**
     * Update content
     *
     * @param content
     * @return a content
     */
    @Override
    public Content updateContent(Content content) throws IOException {
        Content savedContent = contentRepository.save(content);
        log.debug("Updated Content: {}", savedContent);
        try {
            contentElasticsearchService.updateContent(savedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedContent;
    }

    /**
     * Find by contentId
     *
     * @param contentId
     * @return a content
     */
    @Override
    @Transactional(readOnly = true)
    public Content findById(String contentId) throws IOException {
        return contentElasticsearchService.findById(contentId);
    }

    /**
     * Delete by contentId
     *
     * @param contentId
     */
    @Override
    public void delete(String contentId) throws IOException {
        contentRepository.deleteById(contentId);
        log.debug("Deleted Content with id: {}", contentId);
        try {
            contentElasticsearchService.deleteContent(contentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Search By name
     *
     * @param query
     * @return a list of contents
     */
    @Override
    @Transactional(readOnly = true)
    public List<Content> searchByName(String query) throws IOException {
        return contentElasticsearchService.findContentsByName(query);
    }
}
