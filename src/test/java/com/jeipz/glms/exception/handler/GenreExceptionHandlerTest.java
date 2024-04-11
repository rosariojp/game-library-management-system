package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.GenreAlreadyExistsException;
import com.jeipz.glms.exception.GenreNotFoundException;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.service.GenreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class GenreExceptionHandlerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private GenreService genreService;

    @Test
    public void handleGameNotFoundException_test() {
        when(genreService.deleteGenre(any(UUID.class)))
                .thenThrow(GenreNotFoundException.class);

        String document = """
                mutation DeleteGenre($id: ID!) {
                    deleteGenre(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", UUID.randomUUID())
                .execute()
                .errors()
                .expect(result -> Objects.equals("Genre not found.", result.getMessage()));
    }

    @Test
    public void handleGameAlreadyExistsException_test() {
        GenreInput genreInput = new GenreInput("Genre Name");

        when(genreService.addGenre(genreInput))
                .thenThrow(GenreAlreadyExistsException.class);

        Map<String, Object> genreInputMap = new HashMap<>();
        genreInputMap.put("name", genreInput.name());

        String document = """
                mutation AddGenre($genreInput: GenreInput!) {
                    addGenre(genreInput: $genreInput) {
                        id
                        name
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("genreInput", genreInputMap)
                .execute()
                .errors()
                .expect(result ->
                    Objects.equals("Genre name already exists.", result.getMessage())
                );
    }

}