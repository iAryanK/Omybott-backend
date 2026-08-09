package com.aryan.omybott.repositories;

import com.aryan.omybott.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findByOwner_Id(UUID id);

    boolean existsByOwner_IdAndSlug(UUID ownerId, String slug);
}
