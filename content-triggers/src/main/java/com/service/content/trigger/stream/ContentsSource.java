package com.service.content.trigger.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.beans.ConstructorProperties;

@Slf4j
@Component
public class ContentsSource {

    private final ContentProcessor contentProcessor;

    @Autowired
    @ConstructorProperties({"contentProcessor"})
    public ContentsSource(ContentProcessor contentProcessor) {
        this.contentProcessor = contentProcessor;
    }

    public void sendMessage(ContentMessage contentMessage) {
        contentProcessor.contentsOut().send(new GenericMessage<>(contentMessage));
        log.info("Event sent: " + contentMessage.toString());
    }

}
