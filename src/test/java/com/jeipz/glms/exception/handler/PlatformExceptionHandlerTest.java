package com.jeipz.glms.exception.handler;

import com.jeipz.glms.exception.PlatformAlreadyExistsException;
import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.service.PlatformService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureGraphQlTester
class PlatformExceptionHandlerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private PlatformService platformService;

    @Test
    public void handleGameNotFoundException_test() {
        when(platformService.deletePlatform(any(UUID.class)))
                .thenThrow(PlatformNotFoundException.class);

        String document = """
                mutation DeletePlatform($id: ID!) {
                    deletePlatform(id: $id)
                }
                """;

        graphQlTester.document(document)
                .variable("id", UUID.randomUUID())
                .execute()
                .errors()
                .expect(result -> Objects.equals("Platform not found.", result.getMessage()));
    }

    @Test
    public void handleGameAlreadyExistsException_test() {
        PlatformInput platformInput = new PlatformInput(
                "Platform Name", "Platform Description");

        when(platformService.addPlatform(platformInput))
                .thenThrow(PlatformAlreadyExistsException.class);

        Map<String, Object> platformInputMap = new HashMap<>();
        platformInputMap.put("name", platformInput.name());
        platformInputMap.put("description", platformInput.description());

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
                .errors()
                .expect(result ->
                    Objects.equals("Platform name already exists.", result.getMessage())
                );
    }

}