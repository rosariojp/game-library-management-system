package com.jeipz.glms.controller;

import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @QueryMapping
    public Page<Platform> getAllPlatforms(@Argument int page,
                                          @Argument int size) {
        return platformService.getAllPlatforms(page, size);
    }

    @QueryMapping
    public Platform getPlatformById(@Argument UUID id) {
        return platformService.getPlatformById(id);
    }

    @MutationMapping
    public Platform addPlatform(@Argument @Valid PlatformInput platformInput) {
        return platformService.addPlatform(platformInput);
    }

    @MutationMapping
    public Platform updatePlatform(@Argument UUID id,
                                   @Argument @Valid PlatformInput platformInput) {
        return platformService.updatePlatform(id, platformInput);
    }

    @MutationMapping
    public String deletePlatform(@Argument UUID id) {
        return platformService.deletePlatform(id);
    }

}
