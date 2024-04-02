package com.jeipz.glms.mapper;

import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PlatformMapper implements Function<PlatformInput, Platform> {

    @Override
    public Platform apply(PlatformInput platformInput) {
        return Platform.builder()
                .name(platformInput.name())
                .description(platformInput.description())
                .build();
    }
}
