package com.jeipz.glms.controller;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class GameControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private GameService gameService;

    private static final int PAGE = 0;

    private static final int SIZE = 0;

    private List<Game> createGameList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Game.builder()
                        .id(UUID.randomUUID())
                        .title("Game " + i)
                        .description("Game Description")
                        .releaseDate(LocalDate.of(2024, 12, 31))
                        .price(new BigDecimal("59.99"))
                        .genres(new HashSet<>())
                        .platforms(new HashSet<>())
                        .build())
                .toList();
    }

    @Test
    public void getAllGames_test() {
        List<Game> gameList = createGameList();
        Page<Game> gamePages = new PageImpl<>(gameList);
        PageResponse<Game> gamePageResponse = new PageResponse<>(
          gamePages.getContent(),
          gamePages.getNumber() + 1,
          gamePages.getTotalPages(),
          gamePages.getTotalElements()
        );

        when(gameService.getAllGames(PAGE, SIZE))
                .thenReturn(gamePageResponse);

        String document = """
                query GetAllGames($page: Int!, $size: Int!) {
                    getAllGames(page: $page, size: $size) {
                        content {
                            id
                            title
                            description
                            releaseDate
                            price
                        }
                        currentPage
                        totalPages
                        totalElements
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("page", PAGE)
                .variable("size", SIZE)
                .execute()
                .path("getAllGames")
                .entity(new ParameterizedTypeReference<PageResponse<Game>>() {})
                .satisfies(result -> {
                   assertEquals(gamePageResponse.content().size(), result.content().size());
                    assertEquals(gamePageResponse.currentPage(), result.currentPage());
                    assertEquals(gamePageResponse.totalPages(), result.totalPages());
                    assertEquals(gamePageResponse.totalElements(), result.totalElements());
                });
    }

    @Test
    public void getGameById_test() {
        UUID id = UUID.randomUUID();
        Game game = Game.builder()
                .id(id)
                .title("Game Title")
                .description("Game Description")
                .releaseDate(LocalDate.of(2024, 12, 31))
                .price(new BigDecimal("59.99"))
                .build();

        when(gameService.getGameById(id)).thenReturn(game);

        String document = """
                query GetGameById($id: ID!) {
                    getGameById(id: $id) {
                        id
                        title
                        description
                        releaseDate
                        price
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("getGameById")
                .entity(Game.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertEquals(game.getId(), result.getId());
                    assertEquals(game.getTitle(), result.getTitle());
                    assertEquals(game.getDescription(), result.getDescription());
                    assertEquals(game.getReleaseDate(), result.getReleaseDate());
                    assertEquals(game.getPrice(), result.getPrice());
                });
    }

    @Test
    public void addGame_test() {
        GameInput gameInput = new GameInput(
                "Game Title",
                "Game Description",
                LocalDate.of(2024,12,31),
                new BigDecimal("59.99"),
                null,
                null
        );

        Game game = Game.builder()
                .id(UUID.randomUUID())
                .title(gameInput.title())
                .description(gameInput.description())
                .releaseDate(gameInput.releaseDate())
                .price(gameInput.price())
                .build();

        when(gameService.addGame(gameInput))
                .thenReturn(game);

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
                .path("addGame")
                .entity(Game.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(game.getTitle(), result.getTitle());
                    assertEquals(game.getDescription(), result.getDescription());
                    assertEquals(game.getReleaseDate(), result.getReleaseDate());
                    assertEquals(game.getPrice(), result.getPrice());
                });
    }

    @Test
    public void updateGame_test() {
        UUID id = UUID.randomUUID();
        GameInput gameInput = new GameInput(
                "Game Title",
                "Game Description",
                LocalDate.of(2024,12,31),
                new BigDecimal("59.99"),
                null,
                null
        );
        Game game = Game.builder()
                .id(id)
                .title(gameInput.title())
                .description(gameInput.description())
                .releaseDate(gameInput.releaseDate())
                .price(gameInput.price())
                .build();

        when(gameService.updateGame(id, gameInput)).thenReturn(game);

        Map<String, Object> gameInputMap = new HashMap<>();
        gameInputMap.put("title", gameInput.title());
        gameInputMap.put("description", gameInput.description());
        gameInputMap.put("releaseDate", gameInput.releaseDate());
        gameInputMap.put("price", gameInput.price());

        String document = """
                mutation UpdateGame($id: ID!, $gameInput: GameInput!) {
                    updateGame(id: $id, gameInput: $gameInput) {
                        id
                        title
                        description
                        releaseDate
                        price
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .variable("gameInput", gameInputMap)
                .execute()
                .path("updateGame")
                .entity(Game.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertEquals(game.getId(), result.getId());
                    assertEquals(game.getTitle(), result.getTitle());
                    assertEquals(game.getDescription(), result.getDescription());
                    assertEquals(game.getReleaseDate(), result.getReleaseDate());
                    assertEquals(game.getPrice(), result.getPrice());
                });
    }

    @Test
    public void deleteGame_test() {
        UUID id = UUID.randomUUID();
        String message = "Game deleted with id -> " + id;

        when(gameService.deleteGame(id)).thenReturn(message);

        String document = """
                mutation DeleteGame($id: ID!) {
                    deleteGame(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("deleteGame")
                .entity(String.class)
                .satisfies(result -> assertEquals(message, result));
    }
}