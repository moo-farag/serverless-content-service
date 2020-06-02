package com.service.content.trigger.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ContentProcessor {

    String INPUT = "contentsIn";

    @Output
    MessageChannel contentsOut();

    @Input
    SubscribableChannel contentsIn();

}
