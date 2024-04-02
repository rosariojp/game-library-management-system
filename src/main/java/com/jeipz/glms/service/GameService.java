package com.jeipz.glms.service;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.input.GameInput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GameService {

    Page<Game> getAllGames(int page, int size);
    Game getGameById(UUID id);
    Game addGame(GameInput gameInput);
    Game updateGame(UUID id, GameInput gameInput);
    String deleteGame(UUID id);
}
