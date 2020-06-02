package com.service.content.platform.web.rest;


import com.service.content.platform.model.Content;
import com.service.content.platform.service.ContentService;
import com.service.content.platform.utils.HeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@RestController
@EnableWebMvc
@Slf4j
public class ContentResource {

    private ContentService contentService;

    private final String ENTITY_NAME = Content.class.getName();

    @Autowired
    @ConstructorProperties({"contentService"})
    public ContentResource(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * GET /contents : Gets all Contents.
     *
     * @return the ResponseEntity with status 200 (OK) and with body list of all contents
     */
    @GetMapping(path = "/contents")
    public ResponseEntity<List<Content>> getContents() throws IOException {
        return ResponseEntity.ok(contentService.getAll());
    }

    /**
     * POST /contents : Creates a new Content.
     *
     * @param content the content to create
     * @return the ResponseEntity with status 200 (OK) and with body the created content
     */
    @PostMapping(path = "/contents")
    public ResponseEntity<Content> createContent(@RequestBody Content content) throws URISyntaxException, IOException {
        log.debug("REST request to create Content : {}", content);
        Content result = contentService.createContent(content);
        return ResponseEntity.created(new URI("/api/contents/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId()))
                .body(result);
    }

    /**
     * PUT /contents : Updates an existing Content.
     *
     * @param content the content to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated content
     */
    @PutMapping(path = "/contents")
    public ResponseEntity<Content> updateContent(@RequestBody Content content) throws URISyntaxException, IOException {
        if (content.getId() == null) {
            return createContent(content);
        }
        log.debug("REST request to update Content : {}", content);
        Content result = contentService.updateContent(content);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId()))
                .body(result);
    }

    /**
     * DELETE /contents/:id : Deletes an existing Content.
     *
     * @param id the id of the content to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping(path = "/contents/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable(name = "id") String id) throws IOException {
        log.debug("REST request to delete Content with id : {}", id);
        contentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * GET /contents/:id : Gets a Content by id.
     *
     * @param id the id of the content to get
     * @return the ResponseEntity with status 200 (OK) and with body the content
     */
    @GetMapping(path = "/contents/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable(name = "id") String id) throws IOException {
        Content content = contentService.findById(id);
        if (content != null)
            return ResponseEntity.ok(content);
        return ResponseEntity.notFound().build();
    }

    /**
     * SEARCH /contents/search?name=:name : search for Contents by name corresponding
     * to the name param.
     *
     * @param name the name used to search in Contents
     * @return the ResponseEntity with status 200 (OK) and with body result of the search contents
     */
    @GetMapping(path = "/contents/search")
    public ResponseEntity<List<Content>> searchContents(@RequestParam(name = "name") String name) throws IOException {
        return ResponseEntity.ok(contentService.searchByName(name));
    }

}
