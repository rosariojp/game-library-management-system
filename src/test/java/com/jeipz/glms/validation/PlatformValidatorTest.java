package com.jeipz.glms.validation;

import com.jeipz.glms.exception.PlatformAlreadyExistsException;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.repository.PlatformRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PlatformValidatorTest {

    private static final String PLATFORM_NAME = "PS5";
    private static final String PLATFORM_DESCRIPTION = "Playstation 5";
    @InjectMocks
    private PlatformValidator platformValidator;

    @Mock
    private PlatformRepository platformRepository;

    @Test
    public void validateName_Successful() {
        Mockito.when(platformRepository.findByName(PLATFORM_NAME))
                .thenReturn(Optional.empty());
        platformValidator.validateName(PLATFORM_NAME);
    }

    @Test
    public void validateName_PlatformAlreadyExistsException() {
        Mockito.when(platformRepository.findByName(PLATFORM_NAME))
                .thenReturn(Optional.of(Platform.builder()
                        .name(PLATFORM_NAME)
                        .description(PLATFORM_DESCRIPTION)
                        .build()));
        assertThrows(PlatformAlreadyExistsException.class,
                () -> platformValidator.validateName(PLATFORM_NAME));
    }

}