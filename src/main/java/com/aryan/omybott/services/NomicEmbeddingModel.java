package com.aryan.omybott.services;

import com.aryan.omybott.config.NomicEmbeddingProperties;
import com.aryan.omybott.util.PgVectorUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NomicEmbeddingModel extends AbstractEmbeddingModel {

    private final RestClient restClient;
    private final NomicEmbeddingProperties properties;

    public NomicEmbeddingModel(RestClient.Builder restClientBuilder, NomicEmbeddingProperties properties) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new IllegalStateException("NOMIC_API_KEY is required for vector embeddings");
        }

        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<float[]> embeddings = embedBatch(request.getInstructions(), properties.getDocumentTaskType());
        List<Embedding> results = new ArrayList<>(embeddings.size());

        for (int index = 0; index < embeddings.size(); index++) {
            results.add(new Embedding(embeddings.get(index), index));
        }

        return new EmbeddingResponse(results);
    }

    @Override
    public float[] embed(String text) {
        return embedBatch(List.of(text), properties.getQueryTaskType()).getFirst();
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        return embedBatch(texts, properties.getDocumentTaskType());
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public int dimensions() {
        return PgVectorUtils.EMBEDDING_DIMENSIONS;
    }

    private List<float[]> embedBatch(List<String> texts, String taskType) {
        if (texts.isEmpty()) {
            return List.of();
        }

        List<float[]> embeddings = new ArrayList<>(texts.size());

        for (int start = 0; start < texts.size(); start += properties.getBatchSize()) {
            int end = Math.min(start + properties.getBatchSize(), texts.size());
            List<String> batch = texts.subList(start, end);
            NomicEmbeddingResponse response = restClient.post()
                    .uri("/v1/embedding/text")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "texts", batch,
                            "model", properties.getModel(),
                            "task_type", taskType,
                            "dimensionality", properties.getDimensionality()
                    ))
                    .retrieve()
                    .body(NomicEmbeddingResponse.class);

            if (response == null || response.embeddings() == null) {
                throw new IllegalStateException("Nomic embedding API returned an empty response");
            }

            for (List<Double> vector : response.embeddings()) {
                embeddings.add(toFloatArray(vector));
            }
        }

        return embeddings;
    }

    private float[] toFloatArray(List<Double> vector) {
        float[] embedding = new float[vector.size()];
        for (int index = 0; index < vector.size(); index++) {
            embedding[index] = vector.get(index).floatValue();
        }
        return embedding;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NomicEmbeddingResponse(
            @JsonProperty("embeddings") List<List<Double>> embeddings
    ) {
    }

}
