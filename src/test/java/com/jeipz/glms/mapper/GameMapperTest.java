package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.repository.PlatformRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {

    @InjectMocks
    private GameMapper gameMapper;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Test
    public void gameInputToGameMapping_noGenreAndPlatform_Successful() {
        GameInput gameInput = new GameInput("Dragon Quest XI S",
                "Dragon Quest Game",
                LocalDate.of(2000, 10, 30),
                new BigDecimal("59.99"),
                null,
                null);

        Game game = gameMapper.apply(gameInput);

        assertAll("GameInput properties must be equal to Game properties",
                () -> assertEquals(gameInput.title(), game.getTitle()),
                () -> assertEquals(gameInput.description(), game.getDescription()),
                () -> assertEquals(gameInput.releaseDate(), game.getReleaseDate()),
                () -> assertEquals(gameInput.price(), game.getPrice()));
    }

    @Test
    public void gameInputToGameMapping_withGenreAndPlatform_Successful() {
        UUID genreId = UUID.randomUUID();
        UUID platformId = UUID.randomUUID();
        List<UUID> genreIds = new ArrayList<>(Collections.singleton(genreId));
        List<UUID> platformIds = new ArrayList<>(Collections.singleton(platformId));

        GameInput gameInput = new GameInput("Dragon Quest XI S",
                "Dragon Quest Game",
                LocalDate.of(2000, 10, 30),
                new BigDecimal("59.99"),
                genreIds,
                platformIds);

        Mockito.when(genreRepository.findAllById(genreIds))
                .thenReturn(List.of(Genre.builder().id(genreId).build()));
        Mockito.when(platformRepository.findAllById(platformIds))
                .thenReturn(List.of(Platform.builder().id(platformId).build()));

        Game game = gameMapper.apply(gameInput);

        assertAll("GameInput properties must be equal to Game properties",
                () -> assertEquals(gameInput.title(), game.getTitle()),
                () -> assertEquals(gameInput.description(), game.getDescription()),
                () -> assertEquals(gameInput.releaseDate(), game.getReleaseDate()),
                () -> assertEquals(gameInput.price(), game.getPrice()),
                () -> assertEquals(gameInput.genreIds().size(), game.getGenres().size()),
                () -> assertEquals(gameInput.platformIds().size(), game.getPlatforms().size()));
    }

}