package com.jeipz.glms.controller;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.service.GameService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @QueryMapping
    public Page<Game> getAllGames(@Argument int page,
                                  @Argument int size) {
        return gameService.getAllGames(page, size);
    }

    @QueryMapping
    public Game getGameById(@Argument UUID id) {
        return gameService.getGameById(id);
    }

    @MutationMapping
    public Game addGame(@Argument @Valid GameInput gameInput) {
        return gameService.addGame(gameInput);
    }

    @MutationMapping
    public Game updateGame(@Argument UUID id,
                           @Argument @Valid GameInput gameInput) {
        return gameService.updateGame(id, gameInput);
    }

    @MutationMapping
    public String deleteGame(@Argument UUID id) {
        return gameService.deleteGame(id);
    }

}
