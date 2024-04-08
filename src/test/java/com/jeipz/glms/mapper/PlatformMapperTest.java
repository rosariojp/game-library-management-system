package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformMapperTest {

    private final PlatformMapper platformMapper = new PlatformMapper();

    @Test
    public void platformInputToPlatformMapping_Successful() {
        PlatformInput platformInput = new PlatformInput("PS5", "Playstation 5");
        Platform platform = platformMapper.apply(platformInput);
        assertEquals(platformInput.name(), platform.getName());
        assertEquals(platformInput.description(), platform.getDescription());
    }

}