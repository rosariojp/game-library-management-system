package com.jeipz.glms.service;

import com.jeipz.glms.exception.GenreNotFoundException;
import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.mapper.GenreMapper;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
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
    private final GenreMapper genreMapper;
    private final GenreValidator genreValidator;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper, GenreValidator genreValidator) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
        this.genreValidator = genreValidator;
    }

    @Override
    public Page<Genre> getAllGenres(int page, int size) {
        String field = "name";
        Sort sort = Sort.by(Sort.Order.asc(field));
        Pageable pageable = PageRequest.of(page, size, sort);
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre getGenreById(UUID id) {
        return genreRepository.findById(id)
                .orElseThrow(PlatformNotFoundException::new);
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
        genreRepository.delete(genre);
        return "Genre deleted with id -> " + id;
    }
}
