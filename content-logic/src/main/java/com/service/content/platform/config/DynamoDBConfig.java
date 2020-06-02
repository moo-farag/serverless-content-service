package com.service.content.platform.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.util.StringUtils;
import com.service.content.platform.model.Content;
import com.service.content.platform.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableDynamoDBRepositories(dynamoDBMapperConfigRef = "dynamoDBMapperConfig", basePackageClasses = ContentRepository.class)
public class DynamoDBConfig {

    @Value("${amazon.dynamodb.region}")
    private String dynamoDBRegion;
    @Value("${contentsTableName}")
    private String contentsTableName;

    // The following will be used in dev profile only
    @Value("${amazon.dynamodb.endpoint}")
    private String dBEndpoint;
    @Value("${amazon.aws.accesskey}")
    private String accessKey;
    @Value("${amazon.aws.secretkey}")
    private String secretKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        @SuppressWarnings("deprecation")
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient();
        if (!StringUtils.isNullOrEmpty(dBEndpoint) && !StringUtils.isNullOrEmpty(accessKey) && !StringUtils.isNullOrEmpty(secretKey)) {
            amazonDynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials(accessKey, secretKey));
            amazonDynamoDB.setEndpoint(dBEndpoint);
            if (TableUtils.createTableIfNotExists(amazonDynamoDB, new DynamoDBMapper(amazonDynamoDB).generateCreateTableRequest(Content.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(2L, 2L)))) {
                log.info("~~~ Waiting for Contents table to be active ~~~");
            } else {
                log.info("~~~ Contents table already exists ~~~");
            }
            return amazonDynamoDB;
        }
        amazonDynamoDB.setRegion(Region.getRegion(Regions.fromName(dynamoDBRegion)));
        return amazonDynamoDB;
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride tableNameOverrider) {
        DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
        builder.setTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(contentsTableName));
        return new DynamoDBMapperConfig(DynamoDBMapperConfig.DEFAULT, builder.build());
    }

    @Bean
    public DynamoDBMapperConfig.TableNameOverride tableNameOverrider() {
        return DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(contentsTableName);
    }

}
