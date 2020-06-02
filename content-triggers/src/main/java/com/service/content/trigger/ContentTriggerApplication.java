package com.service.content.trigger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionalSpringApplication;

@SpringBootApplication
public class ContentTriggerApplication {

    public static void main(String[] args) {
        FunctionalSpringApplication.run(ContentTriggerApplication.class, args);
    }
}
