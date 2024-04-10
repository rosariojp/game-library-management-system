package com.jeipz.glms.controller;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.input.GenreInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.service.GenreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
public class GenreControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private GenreService genreService;

    private static final int PAGE = 0;

    private static final int SIZE = 5;

    private List<Genre> createGenreList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Genre.builder()
                        .id(UUID.randomUUID())
                        .name("Genre " + i)
                        .build())
                .toList();
    }

    @Test
    public void getAllGenres_test() throws Exception {
        List<Genre> genreList = createGenreList();
        Page<Genre> genrePages = new PageImpl<>(genreList);

        PageResponse<Genre> genrePageResponses = new PageResponse<>(
                genrePages.getContent(),
                genrePages.getNumber() + 1,
                genrePages.getTotalPages(),
                genrePages.getTotalElements());

        when(genreService.getAllGenres(PAGE, SIZE))
                .thenReturn(genrePageResponses);

        String document = """
                query GetAllGenres($page: Int!, $size: Int!) { 
                    getAllGenres(page: $page, size: $size) {
                        content {
                            id
                            name
                        }
                        currentPage
                        totalPages
                        totalElements
                    }
                }
                
                """;

        graphQlTester.document(document)
                .variable("page", PAGE)
                .variable("size", SIZE)
                .execute()
                .path("getAllGenres")
                .entity(new ParameterizedTypeReference<PageResponse<Genre>>() {})
                .satisfies(result -> {
                    assertEquals(genrePageResponses.content().size(), result.content().size());
                    assertEquals(genrePageResponses.currentPage(), result.currentPage());
                    assertEquals(genrePageResponses.totalPages(), result.totalPages());
                    assertEquals(genrePageResponses.totalElements(), result.totalElements());
                });
    }

    @Test
    public void getGenreById_test() {
        UUID id = UUID.randomUUID();
        Genre genre = Genre.builder()
                .id(id)
                .name("Test Genre")
                .build();

        when(genreService.getGenreById(id)).thenReturn(genre);

        String document = """
                query GetGenreById($id: ID!) {
                    getGenreById(id: $id) {
                        id
                        name
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("getGenreById")
                .entity(Genre.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertEquals(genre.getId(), result.getId());
                    assertEquals(genre.getName(), result.getName());
                });
    }

    @Test
    public void addGenre_test() {
        GenreInput genreInput = new GenreInput("Test Genre");
        Genre genre = Genre.builder()
                .id(UUID.randomUUID())
                .name(genreInput.name())
                .build();

        Map<String, Object> genreInputMap = new HashMap<>();
        genreInputMap.put("name", genreInput.name());

        when(genreService.addGenre(genreInput)).thenReturn(genre);

        String document = """
                mutation AddGenre($genreInput: GenreInput!) {
                    addGenre(genreInput: $genreInput) {
                        id
                        name
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("genreInput", genreInputMap)
                .execute()
                .path("addGenre")
                .entity(Genre.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(genre.getName(), result.getName());
                });
    }

    @Test
    public void updateGenre_test() {
        UUID id = UUID.randomUUID();
        GenreInput genreInput = new GenreInput("Test Genre");
        Genre genre = Genre.builder()
                .id(UUID.randomUUID())
                .name(genreInput.name())
                .build();

        Map<String, Object> genreInputMap = new HashMap<>();
        genreInputMap.put("name", genreInput.name());

        when(genreService.updateGenre(id, genreInput)).thenReturn(genre);

        String document = """
                mutation UpdateGenre($id: ID!, $genreInput: GenreInput!) {
                    updateGenre(id: $id, genreInput: $genreInput) {
                        id
                        name
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .variable("genreInput", genreInputMap)
                .execute()
                .path("updateGenre")
                .entity(Genre.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(genre.getName(), result.getName());
                });
    }

    @Test
    public void deleteGenre_test() {
        UUID id = UUID.randomUUID();
        String message = "Genre deleted with id -> " + id;

        when(genreService.deleteGenre(id)).thenReturn(message);

        String document = """
                mutation DeleteGenre($id: ID!) {
                    deleteGenre(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("deleteGenre")
                .entity(String.class)
                .satisfies(result -> assertEquals(message, result));
    }

}