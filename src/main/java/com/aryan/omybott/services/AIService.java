package com.aryan.omybott.services;

import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.Document;
import com.aryan.omybott.entities.DocumentChunk;
import com.aryan.omybott.repositories.DocumentChunkRepository;
import com.aryan.omybott.util.PgVectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIService {

    private final EmbeddingModel embeddingModel;
    private final DocumentChunkRepository documentChunkRepository;

    public int ingestDocument(Bot bot, Document document, MultipartFile file, String mimeType) throws IOException {
        List<org.springframework.ai.document.Document> sourceDocuments = extractDocuments(file, mimeType);
        List<org.springframework.ai.document.Document> chunks = splitDocuments(sourceDocuments);
        List<String> chunkTexts = chunks.stream()
                .map(org.springframework.ai.document.Document::getText)
                .toList();
        List<float[]> embeddings = embeddingModel.embed(chunkTexts);

        int totalChunks = chunks.size();
        List<DocumentChunk> documentChunks = new ArrayList<>(totalChunks);

        for (int index = 0; index < totalChunks; index++) {
            DocumentChunk documentChunk = new DocumentChunk();
            documentChunk.setBot(bot);
            documentChunk.setDocument(document);
            documentChunk.setChunkIndex(index);
            documentChunk.setTotalChunk(totalChunks);
            documentChunk.setContent(chunkTexts.get(index));
            documentChunk.setEmbedding(embeddings.get(index));
            documentChunks.add(documentChunk);
        }

        documentChunkRepository.saveAll(documentChunks);
        return totalChunks;
    }

    private List<org.springframework.ai.document.Document> extractDocuments(MultipartFile file, String mimeType) throws IOException {
        if ("application/pdf".equals(mimeType)) {
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
            return reader.get();
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (content.isBlank()) {
            throw new IllegalArgumentException("File content is empty");
        }
        return List.of(new org.springframework.ai.document.Document(content));
    }

    private List<org.springframework.ai.document.Document> splitDocuments(List<org.springframework.ai.document.Document> documents) {
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();
        return tokenTextSplitter.apply(documents);
    }

}
