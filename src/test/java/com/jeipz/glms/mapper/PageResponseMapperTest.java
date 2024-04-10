package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.response.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PageResponseMapperTest {

    private final PageResponseMapper<Genre> genrePageResponseMapper = new PageResponseMapper<>();

    private final PageResponseMapper<Platform> platformPageResponseMapper = new PageResponseMapper<>();

    private final PageResponseMapper<Game> gamePageResponseMapper = new PageResponseMapper<>();
    
    @Test
    public void apply_genre() {
        List<Genre> genreList = IntStream.range(0, 3)
                .mapToObj(i -> Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Genre " + i)
                        .build())
                .toList();
        Page<Genre> genrePages = new PageImpl<>(genreList);
        PageResponse<Genre> genrePageResponse = new PageResponse<>(
                genrePages.getContent(),
                genrePages.getNumber() + 1,
                genrePages.getTotalPages(),
                genrePages.getTotalElements()
        );

        PageResponse<Genre> result = genrePageResponseMapper.apply(genrePages);

        assertEquals(genrePageResponse.content(), result.content());
        assertEquals(genrePageResponse.currentPage(), result.currentPage());
        assertEquals(genrePageResponse.totalPages(), result.totalPages());
        assertEquals(genrePageResponse.totalElements(), result.totalElements());
    }

    @Test
    public void apply_platform() {
        List<Platform> platformList = IntStream.range(0, 3)
                .mapToObj(i -> Platform.builder()
                        .id(UUID.randomUUID())
                        .name("Platform " + i)
                        .build())
                .toList();
        Page<Platform> platformPages = new PageImpl<>(platformList);
        PageResponse<Platform> platformPageResponse = new PageResponse<>(
                platformPages.getContent(),
                platformPages.getNumber() + 1,
                platformPages.getTotalPages(),
                platformPages.getTotalElements()
        );

        PageResponse<Platform> result = platformPageResponseMapper.apply(platformPages);

        assertEquals(platformPageResponse.content(), result.content());
        assertEquals(platformPageResponse.currentPage(), result.currentPage());
        assertEquals(platformPageResponse.totalPages(), result.totalPages());
        assertEquals(platformPageResponse.totalElements(), result.totalElements());
    }

    @Test
    public void apply_game() {
        List<Game> gameList = IntStream.range(0, 3)
                .mapToObj(i -> Game.builder()
                        .id(UUID.randomUUID())
                        .title("Game " + i)
                        .price(new BigDecimal("1.25"))
                        .build())
                .toList();
        Page<Game> gamePages = new PageImpl<>(gameList);
        PageResponse<Game> gamePageResponse = new PageResponse<>(
                gamePages.getContent(),
                gamePages.getNumber() + 1,
                gamePages.getTotalPages(),
                gamePages.getTotalElements()
        );

        PageResponse<Game> result = gamePageResponseMapper.apply(gamePages);

        assertEquals(gamePageResponse.content(), result.content());
        assertEquals(gamePageResponse.currentPage(), result.currentPage());
        assertEquals(gamePageResponse.totalPages(), result.totalPages());
        assertEquals(gamePageResponse.totalElements(), result.totalElements());
    }

}