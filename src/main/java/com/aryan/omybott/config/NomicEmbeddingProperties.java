package com.aryan.omybott.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nomic.embedding")
public class NomicEmbeddingProperties {

    private String apiKey;
    private String baseUrl = "https://api-atlas.nomic.ai";
    private String model = "nomic-embed-text-v1.5";
    private String documentTaskType = "search_document";
    private String queryTaskType = "search_query";
    private int dimensionality = 768;
    private int batchSize = 10;

}
