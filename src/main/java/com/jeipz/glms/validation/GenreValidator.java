package com.jeipz.glms.validation;

import com.jeipz.glms.exception.GenreAlreadyExistsException;
import com.jeipz.glms.repository.GenreRepository;
import org.springframework.stereotype.Component;

@Component
public class GenreValidator {

    private final GenreRepository genreRepository;

    public GenreValidator(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public void validateName(String name) {
        genreRepository.findByName(name)
                .ifPresent(platform -> {
                    String message = "Genre name already exists.";
                    throw new GenreAlreadyExistsException(message);
                });
    }

}
