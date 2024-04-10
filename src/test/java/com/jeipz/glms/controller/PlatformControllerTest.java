package com.jeipz.glms.controller;

import com.jeipz.glms.model.Genre;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.service.PlatformService;
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
class PlatformControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private PlatformService platformService;

    private static final int PAGE = 0;

    private static final int SIZE = 5;

    private List<Platform> createPlatformList() {
        return IntStream.range(0, SIZE)
                .mapToObj(i -> Platform.builder()
                        .id(UUID.randomUUID())
                        .name("Platform " + i)
                        .description("Platform Description")
                        .build())
                .toList();
    }

    @Test
    public void getAllPlatforms_test() throws Exception {
        List<Platform> platformList = createPlatformList();
        Page<Platform> platformPages = new PageImpl<>(platformList);

        PageResponse<Platform> platformPageResponse = new PageResponse<>(
                platformPages.getContent(),
                platformPages.getNumber() + 1,
                platformPages.getTotalPages(),
                platformPages.getTotalElements());

        when(platformService.getAllPlatforms(PAGE, SIZE))
                .thenReturn(platformPageResponse);

        String document = """
                query GetAllPlatforms($page: Int!, $size: Int!) { 
                    getAllPlatforms(page: $page, size: $size) {
                        content {
                            id
                            name
                            description
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
                .path("getAllPlatforms")
                .entity(new ParameterizedTypeReference<PageResponse<Genre>>() {})
                .satisfies(result -> {
                    assertEquals(platformPageResponse.content().size(), result.content().size());
                    assertEquals(platformPageResponse.currentPage(), result.currentPage());
                    assertEquals(platformPageResponse.totalPages(), result.totalPages());
                    assertEquals(platformPageResponse.totalElements(), result.totalElements());
                });
    }

    @Test
    public void getPlatformById_test() {
        UUID id = UUID.randomUUID();
        Platform platform = Platform.builder()
                .id(id)
                .name("Test Platform")
                .description("Test Platform Description")
                .build();

        when(platformService.getPlatformById(id)).thenReturn(platform);

        String document = """
                query GetPlatformById($id: ID!) {
                    getPlatformById(id: $id) {
                        id
                        name
                        description
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("getPlatformById")
                .entity(Platform.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertEquals(platform.getId(), result.getId());
                    assertEquals(platform.getName(), result.getName());
                    assertEquals(platform.getDescription(), result.getDescription());
                });
    }

    @Test
    public void addGenre_test() {
        PlatformInput platformInput = new PlatformInput(
                "Test Platform",
                "Test Platform Description");

        Platform platform = Platform.builder()
                .id(UUID.randomUUID())
                .name(platformInput.name())
                .description(platformInput.description())
                .build();

        Map<String, Object> platformInputMap = new HashMap<>();
        platformInputMap.put("name", platformInput.name());
        platformInputMap.put("description", platformInput.description());

        when(platformService.addPlatform(platformInput)).thenReturn(platform);

        String document = """
                mutation AddPlatform($platformInput: PlatformInput!) {
                    addPlatform(platformInput: $platformInput) {
                        id
                        name
                        description
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("platformInput", platformInputMap)
                .execute()
                .path("addPlatform")
                .entity(Platform.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(platform.getName(), result.getName());
                    assertEquals(platform.getDescription(), result.getDescription());
                });
    }

    @Test
    public void updateGenre_test() {
        UUID id = UUID.randomUUID();

        PlatformInput platformInput = new PlatformInput(
                "Test Platform",
                "Test Platform Description");

        Platform platform = Platform.builder()
                .id(id)
                .name(platformInput.name())
                .description(platformInput.description())
                .build();

        Map<String, Object> platformInputMap = new HashMap<>();
        platformInputMap.put("name", platformInput.name());
        platformInputMap.put("description", platformInput.description());

        when(platformService.updatePlatform(id, platformInput)).thenReturn(platform);

        String document = """
                mutation UpdatePlatform($id: ID!, $platformInput: PlatformInput!) {
                    updatePlatform(id: $id, platformInput: $platformInput) {
                        id
                        name
                        description
                    }
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .variable("platformInput", platformInputMap)
                .execute()
                .path("updatePlatform")
                .entity(Platform.class)
                .satisfies(result -> {
                    assertNotNull(result);
                    assertNotNull(result.getId());
                    assertEquals(platform.getName(), result.getName());
                    assertEquals(platform.getDescription(), result.getDescription());
                });
    }

    @Test
    public void deleteGenre_test() {
        UUID id = UUID.randomUUID();
        String message = "Platform deleted with id -> " + id;

        when(platformService.deletePlatform(id)).thenReturn(message);

        String document = """
                mutation DeletePlatform($id: ID!) {
                    deletePlatform(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", id)
                .execute()
                .path("deletePlatform")
                .entity(String.class)
                .satisfies(result -> assertEquals(message, result));
    }
}