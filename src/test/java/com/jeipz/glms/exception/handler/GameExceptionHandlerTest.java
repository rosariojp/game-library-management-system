package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.GameAlreadyExistsException;
import com.jeipz.glms.exception.GameNotFoundException;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class GameExceptionHandlerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private GameService gameService;

    @Test
    public void handleGameNotFoundException_test() {
        when(gameService.deleteGame(any(UUID.class)))
                .thenThrow(GameNotFoundException.class);

        String document = """
                mutation DeleteGame($id: ID!) {
                    deleteGame(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", UUID.randomUUID())
                .execute()
                .errors()
                .expect(result -> Objects.equals("Game not found.", result.getMessage()));
    }

    @Test
    public void handleGameAlreadyExistsException_test() {
        GameInput gameInput = new GameInput(
                "Game Title",
                "Game Description",
                LocalDate.of(2024, 12, 31),
                new BigDecimal("59.99"),
                null,
                null
        );

        when(gameService.addGame(gameInput))
                .thenThrow(GameAlreadyExistsException.class);

        Map<String, Object> gameInputMap = new HashMap<>();
        gameInputMap.put("title", gameInput.title());
        gameInputMap.put("description", gameInput.description());
        gameInputMap.put("releaseDate", gameInput.releaseDate());
        gameInputMap.put("price", gameInput.price());

        String document = """
                mutation AddGame($gameInput: GameInput!) {
                    addGame(gameInput: $gameInput) {
                        id
                        title
                        description
                        releaseDate
                        price
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("gameInput", gameInputMap)
                .execute()
                .errors()
                .expect(result ->
                    Objects.equals("Game title already exists.", result.getMessage())
                );
    }

}