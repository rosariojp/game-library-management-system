package com.jeipz.glms.service;

import com.jeipz.glms.exception.GenreNotFoundException;
import com.jeipz.glms.mapper.GenreMapper;
import com.jeipz.glms.mapper.PageResponseMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.GenreRepository;
import com.jeipz.glms.validation.GenreValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final GenreMapper genreMapper;
    private final PageResponseMapper<Genre> pageResponseMapper;
    private final GenreValidator genreValidator;

    public GenreServiceImpl(GenreRepository genreRepository, GameRepository gameRepository, GenreMapper genreMapper, PageResponseMapper<Genre> pageResponseMapper, GenreValidator genreValidator) {
        this.genreRepository = genreRepository;
        this.gameRepository = gameRepository;
        this.genreMapper = genreMapper;
        this.pageResponseMapper = pageResponseMapper;
        this.genreValidator = genreValidator;
    }

    @Override
    public PageResponse<Genre> getAllGenres(int page, int size) {
        String field = "name";
        Sort sort = Sort.by(field).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Genre> pages = genreRepository.findAll(pageable);
        return pageResponseMapper.apply(pages);
    }

    @Override
    public Genre getGenreById(UUID id) {
        return genreRepository.findById(id)
                .orElseThrow(GenreNotFoundException::new);
    }

    @Override
    public Genre addGenre(GenreInput genreInput) {
        genreValidator.validateName(genreInput.name());
        Genre newGenre = genreMapper.apply(genreInput);
        return genreRepository.save(newGenre);
    }

    @Override
    public Genre updateGenre(UUID id, GenreInput genreInput) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(GenreNotFoundException::new);

        if (!genre.getName().equals(genreInput.name())) {
            genreValidator.validateName(genreInput.name());
        }

        genre.setName(genreInput.name());
        return genreRepository.save(genre);
    }

    @Override
    public String deleteGenre(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(GenreNotFoundException::new);
        removeAssociationFromGames(genre);
        genreRepository.delete(genre);
        return "Genre deleted with id -> " + id;
    }

    private void removeAssociationFromGames(Genre genre) {
        for (Game game : genre.getGames()) {
            game.getGenres().remove(genre);
            gameRepository.save(game);
        }
    }
}
