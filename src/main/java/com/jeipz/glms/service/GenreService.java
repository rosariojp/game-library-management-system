package com.jeipz.glms.service;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GenreService {

    Page<Genre> getAllGenres(int page, int size);
    Genre getGenreById(UUID id);
    Genre addGenre(GenreInput genreInput);
    Genre updateGenre(UUID id, GenreInput genreInput);
    String deleteGenre(UUID id);
}
