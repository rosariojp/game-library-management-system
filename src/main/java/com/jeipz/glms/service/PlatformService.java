package com.jeipz.glms.service;

import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.model.response.PageResponse;

import java.util.UUID;

public interface PlatformService {

    PageResponse<Platform> getAllPlatforms(int page, int size);
    Platform getPlatformById(UUID id);
    Platform addPlatform(PlatformInput platformInput);
    Platform updatePlatform(UUID id, PlatformInput platformInput);
    String deletePlatform(UUID id);
}
