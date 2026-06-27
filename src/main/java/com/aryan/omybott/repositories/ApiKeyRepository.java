package com.aryan.omybott.repositories;

import com.aryan.omybott.entities.ApiKey;
import com.aryan.omybott.enums.ApiKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByBot_id(UUID botId);

    List<ApiKey> findByStatus(ApiKeyStatus status);
}
