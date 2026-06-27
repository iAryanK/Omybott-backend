package com.aryan.omybott.vectorstore;

import com.aryan.omybott.entities.DocumentChunk;
import com.aryan.omybott.repositories.DocumentChunkRepository;
import com.aryan.omybott.util.PgVectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentChunkVectorStore implements VectorStore {

    private static final ThreadLocal<UUID> CURRENT_BOT_ID = new ThreadLocal<>();

    private final EmbeddingModel embeddingModel;
    private final DocumentChunkRepository documentChunkRepository;

    public void useBotScope(UUID botId) {
        CURRENT_BOT_ID.set(botId);
    }

    public void clearBotScope() {
        CURRENT_BOT_ID.remove();
    }

    @Override
    public void add(List<Document> documents) {
        throw new UnsupportedOperationException("Use DocumentService to ingest documents into the knowledge base");
    }

    @Override
    public void delete(List<String> idList) {
        throw new UnsupportedOperationException("Document chunk deletion is not supported via VectorStore");
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        throw new UnsupportedOperationException("Document chunk deletion is not supported via VectorStore");
    }

    @Override
    public List<Document> similaritySearch(SearchRequest searchRequest) {
        UUID botId = CURRENT_BOT_ID.get();
        if (botId == null) {
            throw new IllegalStateException("Bot scope is not set for knowledge base search");
        }

        float[] queryEmbedding = embeddingModel.embed(searchRequest.getQuery());
        List<DocumentChunk> chunks = documentChunkRepository.findTopSimilarByBotId(
                botId,
                PgVectorUtils.toLiteral(queryEmbedding),
                searchRequest.getTopK()
        );

        return chunks.stream()
                .map(this::toAiDocument)
                .toList();
    }

    private Document toAiDocument(DocumentChunk chunk) {
        return new Document(
                chunk.getContent(),
                Map.of("chunk_index", chunk.getChunkIndex())
        );
    }

}
