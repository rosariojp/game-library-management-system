package com.jeipz.glms.controller;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @QueryMapping
    public Page<Genre> getAllGenres(@Argument int page,
                                    @Argument int size) {
        return genreService.getAllGenres(page, size);
    }

    @QueryMapping
    public Genre getGenreById(@Argument UUID id) {
        return genreService.getGenreById(id);
    }

    @MutationMapping
    public Genre addGenre(@Argument @Valid GenreInput genreInput) {
        return genreService.addGenre(genreInput);
    }

    @MutationMapping
    public Genre updateGenre(@Argument UUID id,
                             @Argument @Valid GenreInput genreInput) {
        return genreService.updateGenre(id, genreInput);
    }

    @MutationMapping
    public String deleteGenre(@Argument UUID id) {
        return genreService.deleteGenre(id);
    }

}
