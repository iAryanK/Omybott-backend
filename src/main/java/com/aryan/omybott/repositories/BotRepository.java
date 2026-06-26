package com.aryan.omybott.repositories;

import com.aryan.omybott.entities.Bot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BotRepository extends JpaRepository<Bot, UUID> {
    List<Bot> findByWorkspace_Id(UUID workspaceId);
}
