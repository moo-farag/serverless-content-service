package com.service.content.trigger.lambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan
public class ContentTriggerConfig {

    @Bean
    public ContentTriggerHandler contentTriggerHandler() {
        return new ContentTriggerHandler();
    }


}
