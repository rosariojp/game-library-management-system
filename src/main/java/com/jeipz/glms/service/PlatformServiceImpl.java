package com.jeipz.glms.service;

import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.mapper.PlatformMapper;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.repository.PlatformRepository;
import com.jeipz.glms.validation.PlatformValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;
    private final PlatformValidator platformValidator;

    public PlatformServiceImpl(PlatformRepository platformRepository, PlatformMapper platformMapper, PlatformValidator platformValidator) {
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
        this.platformValidator = platformValidator;
    }

    @Override
    public Page<Platform> getAllPlatforms(int page, int size) {
        String field = "name";
        Sort sort = Sort.by(Sort.Order.asc(field));
        Pageable pageable = PageRequest.of(page, size, sort);
        return platformRepository.findAll(pageable);
    }

    @Override
    public Platform getPlatformById(UUID id) {
        return platformRepository.findById(id)
                .orElseThrow(PlatformNotFoundException::new);
    }

    @Override
    public Platform addPlatform(PlatformInput platformInput) {
        platformValidator.validateName(platformInput.name());
        Platform newPlatform = platformMapper.apply(platformInput);
        return platformRepository.save(newPlatform);
    }

    @Override
    public Platform updatePlatform(UUID id, PlatformInput platformInput) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(PlatformNotFoundException::new);

        if (!platform.getName().equals(platformInput.name())) {
            platformValidator.validateName(platformInput.name());
        }

        platform.setName(platformInput.name());
        platform.setDescription(platformInput.description());
        return platformRepository.save(platform);
    }

    @Override
    public String deletePlatform(UUID id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(PlatformNotFoundException::new);
        platformRepository.delete(platform);
        return "Platform deleted with id -> " + id;
    }
}
