package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.PlatformAlreadyExistsException;
import com.jeipz.glms.exception.PlatformNotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class PlatformExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handlePlatformNotFoundException(PlatformNotFoundException ex) {
        return GraphQLError.newError()
                .message("Platform not found.")
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handlePlatformAlreadyExistsException(PlatformAlreadyExistsException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }

}
