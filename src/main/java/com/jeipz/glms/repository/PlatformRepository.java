package com.jeipz.glms.repository;

import com.jeipz.glms.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, UUID> {
    Optional<Platform> findByName(String name);
}
