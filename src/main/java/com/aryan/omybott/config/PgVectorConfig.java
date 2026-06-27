package com.aryan.omybott.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgVectorConfig {

    private final DataSource dataSource;

    @PostConstruct
    public void enablePgVectorExtension() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE EXTENSION IF NOT EXISTS vector");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createEmbeddingIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_document_chunk_embedding
                    ON document_chunk USING hnsw (embedding vector_cosine_ops)
                    """);
        } catch (SQLException exception) {
            log.warn("Could not create document_chunk embedding index: {}", exception.getMessage());
        }
    }

}
