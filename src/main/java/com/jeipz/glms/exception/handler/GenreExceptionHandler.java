package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.GenreAlreadyExistsException;
import com.jeipz.glms.exception.GenreNotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GenreExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleGenreNotFoundException(GenreNotFoundException ex) {
        return GraphQLError.newError()
                .message("Genre not found.")
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleGenreAlreadyExistsException(GenreAlreadyExistsException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .build();
    }

}
