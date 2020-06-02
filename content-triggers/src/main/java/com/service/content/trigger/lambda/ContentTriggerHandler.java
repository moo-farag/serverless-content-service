package com.service.content.trigger.lambda;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

public class ContentTriggerHandler extends SpringBootRequestHandler<DynamodbEvent, Response> {
}
