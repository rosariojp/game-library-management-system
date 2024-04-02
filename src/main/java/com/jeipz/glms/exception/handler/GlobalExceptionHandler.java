package com.jeipz.glms.exception.handler;

import graphql.GraphQLError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleConstraintViolationException(ConstraintViolationException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }
}