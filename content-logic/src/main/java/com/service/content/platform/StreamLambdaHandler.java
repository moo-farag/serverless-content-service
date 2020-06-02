package com.service.content.platform;


import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.service.content.platform.filter.CognitoIdentityFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.EnumSet;

/**
 * AWS Lambda handler:
 * <p>
 * Spring Boot 2 applications can be slow to start, particularly if they discover and initialize a lot of components.
 * In the example above, we recommend using a static block or the constructor of your RequestStreamHandler class to initialize
 * the framework to take advantage of the higher CPU available in AWS Lambda during the initialization phase. However,
 * AWS Lambda limits the initialization phase to 10 seconds. If you application takes longer than 10 seconds to start,
 * AWS Lambda will assume the sandbox is dead and attempt to start a new one. To make the most of the 10 seconds available in the initialization,
 * and still return control back to the Lambda runtime in a timely fashion, we support asynchronous initialization
 * <p>
 * Asynchronous initialization is supported if the app takes more than 10 sec to start
 */
public class StreamLambdaHandler implements RequestStreamHandler {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    static {
        long startTime = Instant.now().toEpochMilli();
        LambdaContainerHandler.getContainerConfig().setInitializationTimeout(20_000);
        try {
            handler = new SpringBootProxyHandlerBuilder()
                    .defaultProxy()
                    .asyncInit(startTime)
                    .springBootApplication(ContentLogicApplication.class)
                    .buildAndInitialize();
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
        handler.onStartup(servletContext -> {
            FilterRegistration.Dynamic registration = servletContext.addFilter("CognitoIdentityFilter", CognitoIdentityFilter.class);
            registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        });
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
