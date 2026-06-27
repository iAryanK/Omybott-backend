package com.aryan.omybott.repositories;

import com.aryan.omybott.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByBot_Id(UUID botId);
}
