package com.jeipz.glms.validation;

import com.jeipz.glms.exception.GameAlreadyExistsException;
import com.jeipz.glms.repository.GameRepository;
import org.springframework.stereotype.Component;

@Component
public class GameValidator {

    private final GameRepository gameRepository;

    public GameValidator(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void validateTitle(String title) {
        gameRepository.findByTitle(title)
                .ifPresent(platform -> {
                    String message = "Game title already exists.";
                    throw new GameAlreadyExistsException(message);
                });
    }

}
