package com.service.content.trigger.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * This class is written to mock unused beans by spring-cloud-stream-binder-aws-kinesis 1.0.0.RELEASE
 * As there are no custom configurations for this version and higher versions increased the deployment size a lot
 * without the need of the new features,
 */
@Configuration
public class MockKinesisConsumerConfiguration {

    @Bean
    public AmazonDynamoDBAsync dynamoDB() {
        return null;
    }

    @Bean
    public ConcurrentMetadataStore kinesisCheckpointStore() {
        return new SimpleMetadataStore();
    }

    @Bean
    public LockRegistry dynamoDBLockRegistry() {
        return new DefaultLockRegistry();
    }

}
