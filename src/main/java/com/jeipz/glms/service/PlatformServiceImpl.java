package com.jeipz.glms.service;

import com.jeipz.glms.exception.PlatformNotFoundException;
import com.jeipz.glms.mapper.PageResponseMapper;
import com.jeipz.glms.mapper.PlatformMapper;
import com.jeipz.glms.model.Game;
import com.jeipz.glms.model.Platform;
import com.jeipz.glms.model.input.PlatformInput;
import com.jeipz.glms.model.response.PageResponse;
import com.jeipz.glms.repository.GameRepository;
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
    private final GameRepository gameRepository;
    private final PlatformMapper platformMapper;
    private final PlatformValidator platformValidator;
    private final PageResponseMapper<Platform> pageResponseMapper;

    public PlatformServiceImpl(PlatformRepository platformRepository, GameRepository gameRepository, PlatformMapper platformMapper, PlatformValidator platformValidator, PageResponseMapper<Platform> pageResponseMapper) {
        this.platformRepository = platformRepository;
        this.gameRepository = gameRepository;
        this.platformMapper = platformMapper;
        this.platformValidator = platformValidator;
        this.pageResponseMapper = pageResponseMapper;
    }

    @Override
    public PageResponse<Platform> getAllPlatforms(int page, int size) {
        String field = "name";
        Sort sort = Sort.by(field).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Platform> pages = platformRepository.findAll(pageable);
        return pageResponseMapper.apply(pages);
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
        removeAssociationFromGames(platform);
        platformRepository.delete(platform);
        return "Platform deleted with id -> " + id;
    }

    private void removeAssociationFromGames(Platform platform) {
        for (Game game : platform.getGames()) {
            game.getPlatforms().remove(platform);
            gameRepository.save(game);
        }
    }
}
