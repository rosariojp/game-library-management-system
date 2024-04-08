package com.jeipz.glms.service;

import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.mapper.PlatformMapper;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.repository.GameRepository;
import com.jeipz.glms.repository.PlatformRepository;
import com.jeipz.glms.validation.PlatformValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    private static final int PAGE = 0;

    private static final int SIZE = 5;

    private static final String PLATFORM_NAME = "PS5";

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

}