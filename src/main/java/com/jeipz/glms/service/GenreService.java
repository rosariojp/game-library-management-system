package com.jeipz.glms.service;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.model.response.PageResponse;

import java.util.UUID;

public interface GenreService {

    PageResponse<Genre> getAllGenres(int page, int size);
    Genre getGenreById(UUID id);
    Genre addGenre(GenreInput genreInput);
    Genre updateGenre(UUID id, GenreInput genreInput);
    String deleteGenre(UUID id);
}
