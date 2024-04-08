package com.jeipz.glms.service;

import com.jeipz.glms.exception.PlatformAlreadyExistsException;
import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.mapper.PlatformMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.PlatformRepository;
import com.jeipz.glms.validation.PlatformValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    private static final int PAGE = 0;

    private static final int SIZE = 5;

    private static final String PLATFORM_NAME = "PS5";

    private static final String PLATFORM_DESCRIPTION = "Playstation 5";

    private static final String PLATFORM_NAME_FIELD = "name";

    private static final Sort SORT = Sort.by(PLATFORM_NAME_FIELD).ascending();

    @InjectMocks
    private PlatformServiceImpl platformService;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private PlatformMapper platformMapper;

    @Mock
    private PlatformValidator platformValidator;

    @Mock
    private GameRepository gameRepository;

    private List<Platform> createPlatformList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Platform.builder()
                        .id(UUID.randomUUID())
                        .name("Platform " + i)
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    public void getAllPlatforms_Successful() {
        Pageable pageable = PageRequest.of(PAGE, SIZE, SORT);
        List<Platform> platforms = createPlatformList();
        Page<Platform> platformPages = new PageImpl<>(platforms);

        when(platformRepository.findAll(pageable))
                .thenReturn(platformPages);

        Page<Platform> fetchedPlatformPages = platformService.getAllPlatforms(PAGE, SIZE);

        assertAll("Should return pages of platform in ascending order",
                () -> assertEquals(platformPages, fetchedPlatformPages),
                () -> assertEquals(PAGE, fetchedPlatformPages.getNumber()),
                () -> assertEquals(SIZE, fetchedPlatformPages.getSize()));

        verify(platformRepository, times(1)).findAll(pageable);
    }

    @Test
    public void getPlatformById_Successful() {
        UUID id = UUID.randomUUID();
        Platform platform = Platform.builder()
                .name(PLATFORM_NAME)
                .build();

        when(platformRepository.findById(id))
                .thenReturn(Optional.of(platform));

        Platform fetchedPlatform = platformService.getPlatformById(id);

        assertAll("platform must be equal to fetchedPlatform",
                () -> assertEquals(platform.getId(), fetchedPlatform.getId()),
                () -> assertEquals(platform.getName(), fetchedPlatform.getName()),
                () -> assertEquals(platform.getDescription(), fetchedPlatform.getDescription()));

        verify(platformRepository, times(1)).findById(id);
    }

    @Test
    public void getPlatformById_NotFound() {
        UUID id = UUID.randomUUID();

        when(platformRepository.findById(id)).thenThrow(PlatformNotFoundException.class);

        assertAll("Should return PlatformNotFoundException",
                () -> assertThrows(PlatformNotFoundException.class, () -> platformService.getPlatformById(id)));

        verify(platformRepository, times(1)).findById(id);
    }

    @Test
    public void addPlatform_Successful() {
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);
        Platform platform = Platform.builder()
                .id(UUID.randomUUID())
                .name(platformInput.name())
                .description(platformInput.description())
                .build();

        doNothing().when(platformValidator).validateName(platformInput.name());
        when(platformMapper.apply(platformInput)).thenReturn(platform);
        when(platformRepository.save(platform)).thenReturn(platform);

        Platform savedPlatform = platformService.addPlatform(platformInput);

        assertAll("savedPlatform must be equal to platform",
                () -> assertEquals(platform.getId(), savedPlatform.getId()),
                () -> assertEquals(platform.getName(), savedPlatform.getName()),
                () -> assertEquals(platform.getDescription(), savedPlatform.getDescription()));

        verify(platformValidator, times(1)).validateName(platformInput.name());
        verify(platformMapper, times(1)).apply(platformInput);
        verify(platformRepository, times(1)).save(platform);
    }

    @Test
    public void addPlatform_AlreadyExists() {
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);

        doThrow(PlatformAlreadyExistsException.class).when(platformValidator).validateName(PLATFORM_NAME);

        assertAll("Must throw PlatformAlreadyExistsException",
                () -> assertThrows(PlatformAlreadyExistsException.class, () -> platformService.addPlatform(platformInput)));

        verify(platformValidator, times(1)).validateName(platformInput.name());
    }

    @Test
    public void updatePlatform_withUpdates_Successful() {
        UUID id = UUID.randomUUID();
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);
        Platform platform = Platform.builder()
                .id(id)
                .name("Nintendo Switch")
                .description("Nintendo's latest gaming device")
                .build();
        Platform updatedPlatform = Platform.builder()
                .id(id)
                .name(platformInput.name())
                .description(platformInput.description())
                .build();

        when(platformRepository.findById(id)).thenReturn(Optional.of(platform));
        doNothing().when(platformValidator).validateName(platformInput.name());
        when(platformRepository.save(any(Platform.class))).thenReturn(updatedPlatform);

        Platform resultPlatform = platformService.updatePlatform(id, platformInput);

        assertAll("updatedPlatform must be equal to resultPlatform",
                () -> assertEquals(updatedPlatform.getId(), resultPlatform.getId()),
                () -> assertEquals(updatedPlatform.getName(), resultPlatform.getName()),
                () -> assertEquals(updatedPlatform.getDescription(), resultPlatform.getDescription()));

        verify(platformRepository, times(1)).findById(id);
        verify(platformValidator, times(1)).validateName(platformInput.name());
        verify(platformRepository, times(1)).save(any(Platform.class));
    }

    @Test
    public void updatePlatform_noChanges_Successful() {
        UUID id = UUID.randomUUID();
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);
        Platform platform = Platform.builder()
                .id(id)
                .name("Nintendo Switch")
                .description("Nintendo's latest gaming device")
                .build();

        when(platformRepository.findById(id)).thenReturn(Optional.of(platform));
        when(platformRepository.save(any(Platform.class))).thenReturn(platform);

        Platform resultPlatform = platformService.updatePlatform(id, platformInput);

        assertAll("platform must be equal to resultPlatform",
                () -> assertEquals(platform.getId(), resultPlatform.getId()),
                () -> assertEquals(platform.getName(), resultPlatform.getName()),
                () -> assertEquals(platform.getDescription(), resultPlatform.getDescription()));

        verify(platformRepository, times(1)).findById(id);
        verify(platformRepository, times(1)).save(any(Platform.class));
    }

    @Test
    public void updatePlatform_NotFound() {
        UUID id = UUID.randomUUID();
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);

        doThrow(PlatformNotFoundException.class).when(platformRepository).findById(id);

        assertAll("Must throw PlatformNotFoundException",
                () -> assertThrows(PlatformNotFoundException.class, () -> platformService.updatePlatform(id, platformInput)));

        verify(platformRepository, times(1)).findById(id);
    }

    @Test
    public void updatePlatform_AlreadyExists() {
        UUID id = UUID.randomUUID();
        PlatformInput platformInput = new PlatformInput(PLATFORM_NAME, PLATFORM_DESCRIPTION);
        Platform platform = Platform.builder()
                .id(id)
                .name("Nintendo Switch")
                .description("Nintendo's latest gaming device")
                .build();
        Platform updatedPlatform = Platform.builder()
                .id(id)
                .name(platformInput.name())
                .description(platformInput.description())
                .build();

        when(platformRepository.findById(id)).thenReturn(Optional.of(platform));
        doThrow(PlatformAlreadyExistsException.class).when(platformValidator).validateName(platformInput.name());

        assertAll("Must throw PlatformAlreadyExistsException",
                () -> assertThrows(PlatformAlreadyExistsException.class, () -> platformService.updatePlatform(id, platformInput)));

        verify(platformRepository, times(1)).findById(id);
        verify(platformValidator, times(1)).validateName(platformInput.name());
    }

    private Set<Game> createGameSet(int elemCount) {
        return IntStream.range(0, elemCount)
                .mapToObj(i -> Game.builder()
                        .id(UUID.randomUUID())
                        .title("Game " + i)
                        .price(new BigDecimal("59.99"))
                        .platforms(new HashSet<>())
                        .build())
                .collect(Collectors.toSet());
    }

    @Test
    public void deletePlatform_Successful() {
        UUID id = UUID.randomUUID();
        int gameCount = 3;
        Platform platform = Platform.builder()
                .id(id)
                .name(PLATFORM_NAME)
                .description(PLATFORM_DESCRIPTION)
                .games(createGameSet(gameCount))
                .build();

        when(platformRepository.findById(id)).thenReturn(Optional.of(platform));
        doNothing().when(platformRepository).delete(platform);

        String message = platformService.deletePlatform(id);

        assertAll("Should delete platform",
                () -> assertEquals("Platform deleted with id -> " + id, message));

        verify(platformRepository, times(1)).findById(id);
        verify(gameRepository, times(gameCount)).save(any(Game.class));
        verify(platformRepository, times(1)).delete(platform);
    }

    @Test
    public void deletePlatform_NotFound() {
        UUID id = UUID.randomUUID();

        doThrow(PlatformNotFoundException.class).when(platformRepository).findById(id);

        assertAll("Must throw PlatformNotFoundException",
                () -> assertThrows(PlatformNotFoundException.class, () -> platformService.deletePlatform(id)));

        verify(platformRepository, times(1)).findById(id);
    }

}