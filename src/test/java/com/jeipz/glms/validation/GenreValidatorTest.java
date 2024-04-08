package com.jeipz.glms.validation;

import com.jeipz.glms.exception.GenreAlreadyExistsException;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GenreValidatorTest {

    public static final String GENRE_NAME = "RPG";
    @InjectMocks
    private GenreValidator genreValidator;

    @Mock
    private GenreRepository genreRepository;

    @Test
    public void validateName_Successful() {
        Mockito.when(genreRepository.findByName(GENRE_NAME))
                .thenReturn(Optional.empty());
        genreValidator.validateName(GENRE_NAME);
    }

    @Test
    public void validateName_GenreAlreadyExistsException() {
        Mockito.when(genreRepository.findByName(GENRE_NAME))
                .thenReturn(Optional.of(Genre.builder()
                        .name(GENRE_NAME)
                        .build()));
        assertThrows(GenreAlreadyExistsException.class,
                () -> genreValidator.validateName(GENRE_NAME));
    }

}