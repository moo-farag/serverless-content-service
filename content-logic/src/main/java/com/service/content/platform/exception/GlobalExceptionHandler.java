package com.service.content.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
    @ExceptionHandler(IOException.class)
    public void iOExceptionHandler() {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Content Not Found")
    @ExceptionHandler(ContentNotFoundException.class)
    public void contentNotFoundExceptionHandler() {
    }

}
