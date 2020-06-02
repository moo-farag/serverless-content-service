package com.service.content.trigger.lambda;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.service.content.trigger.stream.MessageOperation;
import com.service.content.trigger.stream.ContentMessage;
import com.service.content.trigger.stream.ContentsSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
@Component
public class ContentTriggerFunction implements Function<DynamodbEvent, Response> {


    private final ContentsSource contentSource;
    @Value("${originator}")
    private String originator;

    public ContentTriggerFunction(final ContentsSource contentSource) {
        this.contentSource = contentSource;
    }

    @Override
    public Response apply(final DynamodbEvent input) {
        try {
            input.getRecords()
                    .forEach(record -> {
                        if ("INSERT".equals(record.getEventName())) {
                            final String contentId = record.getDynamodb().getKeys().get("id").getS();
                            AttributeValue contentName = record.getDynamodb().getNewImage().get("name");
                            log.info("Processing INSERT message for content with id : {" + contentId + "}");
                            contentSource.sendMessage(new ContentMessage(contentId, contentName.getS(), MessageOperation.CREATE, originator));
                            log.info("Processed INSERT Content msg ->  " + record.getDynamodb().getNewImage());
                        } else if ("MODIFY".equals(record.getEventName())) {
                            final String contentId = record.getDynamodb().getKeys().get("id").getS();
                            AttributeValue contentName = record.getDynamodb().getNewImage().get("name");
                            log.info("Processing MODIFY message for content with id : {" + contentId + "}");
                            contentSource.sendMessage(new ContentMessage(contentId, contentName.getS(), MessageOperation.UPDATE, originator));
                            log.info("Processed MODIFY Content msg ->  " + record.getDynamodb().getNewImage());
                        } else if ("REMOVE".equals(record.getEventName())) {
                            final String contentId = record.getDynamodb().getKeys().get("id").getS();
                            log.info("Processing REMOVE message for content with id : {" + contentId + "}");
                            contentSource.sendMessage(new ContentMessage(contentId, MessageOperation.DELETE, originator));
                            log.info("Processed REMOVE Content msg ->  " + record.getDynamodb().getNewImage());
                        }
                    });
            return Response.ok("Content DDB Stream Processed Successfully");
        } catch (Exception e) {
            log.error(getStackTrace(e));
            return Response.error(getStackTrace(e));
        }
    }
}
