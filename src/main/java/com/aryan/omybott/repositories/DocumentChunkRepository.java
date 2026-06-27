package com.aryan.omybott.repositories;

import com.aryan.omybott.entities.Document;
import com.aryan.omybott.entities.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, UUID> {

    @Query(value = """
            SELECT *
            FROM document_chunk
            WHERE bot_id = :botId
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<DocumentChunk> findTopSimilarByBotId(@Param("botId") UUID botId,
                                              @Param("embedding") String embedding,
                                              @Param("limit") int limit);

    List<DocumentChunk> findAllByBot_IdAndDocument_IdOrderByChunkIndexAsc(UUID botId, UUID documentId);
}
