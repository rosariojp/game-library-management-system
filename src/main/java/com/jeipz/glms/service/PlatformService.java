package com.jeipz.glms.service;

import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PlatformService {

    Page<Platform> getAllPlatforms(int page, int size);
    Platform getPlatformById(UUID id);
    Platform addPlatform(PlatformInput platformInput);
    Platform updatePlatform(UUID id, PlatformInput platformInput);
    String deletePlatform(UUID id);
}
