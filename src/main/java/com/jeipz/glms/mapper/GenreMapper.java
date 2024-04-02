package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class GenreMapper implements Function<GenreInput, Genre> {

    @Override
    public Genre apply(GenreInput genreInput) {
        return Genre.builder()
                .name(genreInput.name())
                .build();
    }
}
