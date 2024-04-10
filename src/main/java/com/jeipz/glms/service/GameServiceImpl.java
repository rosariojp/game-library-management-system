package com.jeipz.glms.service;

import com.jeipz.glms.exception.GameNotFoundException;
import com.jeipz.glms.mapper.GameMapper;
import com.jeipz.glms.mapper.PageResponseMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.GameInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.repository.PlatformRepository;
import com.jeipz.glms.validation.GameValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final GameValidator gameValidator;
    private final GameMapper gameMapper;
    private final PageResponseMapper<Game> pageResponseMapper;

    public GameServiceImpl(GameRepository gameRepository, GenreRepository genreRepository, PlatformRepository platformRepository, GameValidator gameValidator, GameMapper gameMapper, PageResponseMapper<Game> pageResponseMapper) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.platformRepository = platformRepository;
        this.gameValidator = gameValidator;
        this.gameMapper = gameMapper;
        this.pageResponseMapper = pageResponseMapper;
    }

    @Override
    public PageResponse<Game> getAllGames(int page, int size) {
        String field = "title";
        Sort sort = Sort.by(Sort.Order.asc(field));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Game> pages = gameRepository.findAll(pageable);
        return pageResponseMapper.apply(pages);
    }

    @Override
    public Game getGameById(UUID id) {
        return gameRepository.findById(id)
                .orElseThrow(GameNotFoundException::new);
    }

    @Override
    public Game addGame(GameInput gameInput) {
        gameValidator.validateTitle(gameInput.title());
        Game game = gameMapper.apply(gameInput);
        return gameRepository.save(game);
    }

    @Override
    public Game updateGame(UUID id, GameInput gameInput) {
        Game game = gameRepository.findById(id)
                .orElseThrow(GameNotFoundException::new);

        if (!game.getTitle().equals(gameInput.title())) {
            gameValidator.validateTitle(gameInput.title());
        }

        game.setTitle(gameInput.title());
        game.setDescription(gameInput.description());
        game.setReleaseDate(gameInput.releaseDate());
        game.setPrice(gameInput.price());
        game.setGenres(createGenres(gameInput.genreIds()));
        game.setPlatforms(createPlatforms(gameInput.platformIds()));
        return gameRepository.save(game);
    }

    @Override
    public String deleteGame(UUID id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(GameNotFoundException::new);
        gameRepository.delete(game);
        return "Game deleted with id -> " + id;
    }

    private Set<Genre> createGenres(List<UUID> genreIds) {
        return genreIds != null && !genreIds.isEmpty() ?
                new HashSet<>(genreRepository.findAllById(genreIds)) :
                new HashSet<>();
    }

    private Set<Platform> createPlatforms(List<UUID> platformIds) {
        return platformIds != null && !platformIds.isEmpty() ?
                new HashSet<>(platformRepository.findAllById(platformIds)) :
                new HashSet<>();
    }
}
