package com.jeipz.glms.service;

import com.jeipz.glms.exception.GameAlreadyExistsException;
import com.jeipz.glms.exception.GameNotFoundException;
import com.jeipz.glms.mapper.GameMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.repository.PlatformRepository;
import com.jeipz.glms.validation.GameValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    private static final String GAME_TITLE_FIELD = "title";

    private static final String GAME_TITLE = "Dragon Quest XI S";

    private static final String GAME_DESCRIPTION = "Dragon Quest Game";

    private static final Sort SORT = Sort.by(GAME_TITLE_FIELD).ascending();

    private static final int PAGE = 0;

    private static final int SIZE = 5;

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private GameValidator gameValidator;

    @Mock
    private GameMapper gameMapper;

    private List<Game> createGameList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Game.builder()
                        .id(UUID.randomUUID())
                        .title("Game " + i)
                        .price(new BigDecimal("59.99"))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Genre> createGenreList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Genre " + i)
                        .build())
                .collect(Collectors.toList());
    }

    private List<Platform> createPlatformList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Platform.builder()
                        .id(UUID.randomUUID())
                        .name("Platform " + i)
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    public void getAllGames_Successful() {
        Pageable pageable = PageRequest.of(PAGE, SIZE, SORT);
        List<Game> gameList = createGameList();
        Page<Game> gamePages = new PageImpl<>(gameList);

        when(gameRepository.findAll(pageable))
                .thenReturn(gamePages);

        Page<Game> fetchedGamePages = gameService.getAllGames(PAGE, SIZE);

        assertAll("Should return pages of games",
                () -> assertEquals(gamePages, fetchedGamePages),
                () -> assertEquals(PAGE, fetchedGamePages.getNumber()),
                () -> assertEquals(SIZE, fetchedGamePages.getSize()));

        verify(gameRepository, times(1))
                .findAll(pageable);
    }

    @Test
    public void getGameById_Successful() {
        UUID id = UUID.randomUUID();
        Game game = Game.builder()
                .id(id)
                .title(GAME_TITLE)
                .price(new BigDecimal("59.99"))
                .build();

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        Game fetchedGame = gameService.getGameById(id);

        assertAll("Should return game",
                () -> assertNotNull(game),
                () -> assertEquals(game.getId(), fetchedGame.getId()),
                () -> assertEquals(game.getTitle(), fetchedGame.getTitle()),
                () -> assertEquals(game.getPrice(), fetchedGame.getPrice()));

        verify(gameRepository, times(1)).findById(id);
    }

    @Test
    public void getGameById_NotFound() {
        UUID id = UUID.randomUUID();

        doThrow(GameNotFoundException.class)
                .when(gameRepository).findById(id);

        assertThrows(GameNotFoundException.class, () -> gameService.getGameById(id));

        verify(gameRepository, times(1)).findById(id);
    }

    @Test
    public void addGame_withoutGenreAndPlatform_Successful() {
        List<Genre> genres = createGenreList();
        List<Platform> platforms = createPlatformList();
        List<UUID> genreIds = genres.stream().map(Genre::getId).toList();
        List<UUID> platformIds = platforms.stream().map(Platform::getId).toList();

        GameInput gameInput = new GameInput(GAME_TITLE,
                GAME_DESCRIPTION,
                LocalDate.of(2024, 12, 30),
                new BigDecimal("59.99"),
                genreIds,
                platformIds);

        Game game = Game.builder()
                .title(gameInput.title())
                .description(gameInput.description())
                .price(gameInput.price())
                .releaseDate(gameInput.releaseDate())
                .platforms(new HashSet<>(platforms))
                .genres(new HashSet<>(genres))
                .build();

        doNothing().when(gameValidator).validateTitle(gameInput.title());
        when(gameMapper.apply(gameInput)).thenReturn(game);
        when(gameRepository.save(game)).thenReturn(game);

        Game savedGame = gameService.addGame(gameInput);

        assertAll("game should be equal to savedGame",
                () -> assertEquals(game.getId(), savedGame.getId()),
                () -> assertEquals(game.getTitle(), savedGame.getTitle()),
                () -> assertEquals(game.getDescription(), savedGame.getDescription()),
                () -> assertEquals(game.getReleaseDate(), savedGame.getReleaseDate()),
                () -> assertEquals(game.getPrice(), savedGame.getPrice()));

        verify(gameValidator, times(1)).validateTitle(gameInput.title());
        verify(gameMapper, times(1)).apply(gameInput);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void addGame_AlreadyExists() {
        GameInput gameInput = new GameInput(GAME_TITLE,
                GAME_DESCRIPTION,
                LocalDate.of(2024, 12, 30),
                new BigDecimal("59.99"),
                null,
                null);

        doThrow(GameAlreadyExistsException.class)
                .when(gameValidator).validateTitle(gameInput.title());

        assertThrows(GameAlreadyExistsException.class,
                () -> gameService.addGame(gameInput));

        verify(gameValidator, times(1))
                .validateTitle(gameInput.title());
    }

}