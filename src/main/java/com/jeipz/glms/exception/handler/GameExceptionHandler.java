package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.GameAlreadyExistsException;
import com.jeipz.glms.exception.GameNotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GameExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleGameNotFoundException(GameNotFoundException ex) {
        return GraphQLError.newError()
                .message("Game not found.")
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleGameAlreadyExistsException(GameAlreadyExistsException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }

}
