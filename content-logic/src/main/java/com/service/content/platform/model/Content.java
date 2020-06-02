package com.service.content.platform.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.service.content.platform.repository.PrefixedUUIDGeneratedKey;

import java.beans.ConstructorProperties;
import java.util.Objects;

/**
 * Table name will be altered upon config (specified in application.yml))
 */
@DynamoDBTable(tableName = "contents-table")
public class Content {

    @DynamoDBHashKey
    private String id;

    /**
     * Always will be lower case currently.
     * Should be handled either here on displayName or
     * to be removed and handled in elasticsearch
     */
    @DynamoDBAttribute
    private String name;

    @ConstructorProperties({})
    public Content() {
    }

    @ConstructorProperties({"id", "name"})
    public Content(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @PrefixedUUIDGeneratedKey(prefix="content-")
    public String getId() {
        return id;
    }

    public Content setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    
    public Content setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return id.equals(content.id) &&
                name.equals(content.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
