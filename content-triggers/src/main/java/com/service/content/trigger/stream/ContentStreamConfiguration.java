package com.service.content.trigger.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@Slf4j
@EnableBinding(ContentProcessor.class)
public class ContentStreamConfiguration {

    @StreamListener(ContentProcessor.INPUT)
    public void processContent(ContentMessage contentMessage) {
        //log the content msg received
        if (!contentMessage.getOriginator().equals("ContentTriggerProducer")) {
            log.info("A content msg has been received " + contentMessage.toString());
            // Fetch content by contentMessage.getContentId() and persist it
        } else {
            log.info("A content msg has been sent " + contentMessage.toString());
        }
    }

}
