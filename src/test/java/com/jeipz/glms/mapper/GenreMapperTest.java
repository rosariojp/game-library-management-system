package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenreMapperTest {

    private final GenreMapper genreMapper = new GenreMapper();

    @Test
    public void genreInputToGenreMapping_Successful() {
        GenreInput genreInput = new GenreInput("RPG");
        Genre genre = genreMapper.apply(genreInput);
        assertEquals(genreInput.name(), genre.getName());
    }

}