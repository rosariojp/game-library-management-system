package com.jeipz.glms.validation;

import com.jeipz.glms.exception.GameAlreadyExistsException;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GameValidatorTest {

    private static final String TITLE_NAME = "Dragon Quest XI S";

    @InjectMocks
    private GameValidator gameValidator;

    @Mock
    private GameRepository gameRepository;

    @Test
    public void validateName_Successful() {
        Mockito.when(gameRepository.findByTitle(TITLE_NAME))
                .thenReturn(Optional.empty());
        gameValidator.validateTitle(TITLE_NAME);
    }

    @Test
    public void validateName_GameAlreadyExistsException() {
        Mockito.when(gameRepository.findByTitle(TITLE_NAME))
                .thenReturn(Optional.of(Game.builder()
                        .title(TITLE_NAME)
                        .price(new BigDecimal("59.99"))
                        .build()));
        assertThrows(GameAlreadyExistsException.class,
                () -> gameValidator.validateTitle(TITLE_NAME));
    }

}