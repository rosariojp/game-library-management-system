package com.jeipz.glms.validation;

import com.jeipz.glms.exception.PlatformAlreadyExistsException;
import com.jeipz.glms.repository.PlatformRepository;
import org.springframework.stereotype.Component;

@Component
public class PlatformValidator {

    private final PlatformRepository platformRepository;

    public PlatformValidator(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public void validateName(String name) {
        platformRepository.findByName(name)
                .ifPresent(platform -> {
                    throw new PlatformAlreadyExistsException();
                });
    }

}
