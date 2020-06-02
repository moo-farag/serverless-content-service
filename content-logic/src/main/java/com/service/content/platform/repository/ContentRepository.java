package com.service.content.platform.repository;

import com.service.content.platform.model.Content;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * the repository for Contents
 * currently scan is enabled to allow search methods to work.
 * we can remove it if we used elasticsearch service
 */
@Repository
@EnableScan
public interface ContentRepository extends CrudRepository<Content, String> {

    List<Content> findAllByNameContaining(String name);

    @Override
    List<Content> findAll();

}
