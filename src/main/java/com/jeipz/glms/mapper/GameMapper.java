package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.repository.PlatformRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Component
public class GameMapper implements Function<GameInput, Game> {

    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;

    public GameMapper(GenreRepository genreRepository, PlatformRepository platformRepository) {
        this.genreRepository = genreRepository;
        this.platformRepository = platformRepository;
    }

    @Override
    public Game apply(GameInput gameInput) {
        Game game = Game.builder()
                .title(gameInput.title())
                .description(gameInput.description())
                .releaseDate(gameInput.releaseDate())
                .price(gameInput.price())
                .build();

        if (gameInput.genreIds() != null
                && !gameInput.genreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(gameInput.genreIds()));
            game.setGenres(genres);
        }

        if (gameInput.platformIds() != null
                && !gameInput.platformIds().isEmpty()) {
            Set<Platform> platforms = new HashSet<>(platformRepository.findAllById(gameInput.platformIds()));
            game.setPlatforms(platforms);
        }

        return game;
    }
}
