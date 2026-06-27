package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.DocReqDTO;
import com.aryan.omybott.dto.response.DocRespDTO;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.Document;
import com.aryan.omybott.entities.DocumentChunk;
import com.aryan.omybott.enums.DocumentStatus;
import com.aryan.omybott.enums.DocumentType;
import com.aryan.omybott.exceptions.ResourceNotFoundException;
import com.aryan.omybott.repositories.DocumentChunkRepository;
import com.aryan.omybott.repositories.DocumentRepository;
import com.aryan.omybott.services.AIService;
import com.aryan.omybott.services.BotService;
import com.aryan.omybott.services.DocumentService;
import com.aryan.omybott.util.DocumentFileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final BotService botService;
    private final ModelMapper modelMapper;
    private final AIService aiService;

    @Override
    public DocRespDTO createAndEmbedDocument(UUID botId, DocReqDTO docReqDTO, MultipartFile file) throws IOException {
        Bot bot = botService.assertAuthorizedToBot(botId);
        DocumentFileUtils.validateFile(file);

        if (docReqDTO == null) {
            docReqDTO = new DocReqDTO();
        }

        String filename = file.getOriginalFilename();
        String mimeType = DocumentFileUtils.resolveMimeType(file, docReqDTO.getMimeType());
        if (!DocumentFileUtils.isSupportedMimeType(mimeType)) {
            throw new IllegalArgumentException("Unsupported file type: " + mimeType);
        }

        DocumentType fileType = docReqDTO.getFileType() != null
                ? docReqDTO.getFileType()
                : DocumentFileUtils.resolveDocumentType(mimeType, filename);

        Document document = modelMapper.map(docReqDTO, Document.class);
        document.setBot(bot);
        document.setFileName(docReqDTO.getFileName() != null ? docReqDTO.getFileName() : filename);
        document.setFileType(fileType);
        document.setMimeType(mimeType);
        document.setFileSizeBytes(docReqDTO.getFileSizeBytes() != null ? docReqDTO.getFileSizeBytes() : file.getSize());
        document.setStatus(DocumentStatus.PROCESSING);
        document = documentRepository.save(document);

        try {
            int chunkCount = aiService.ingestDocument(bot, document, file, mimeType);
            document.setChunkCount(chunkCount);
            document.setStatus(DocumentStatus.READY);
        } catch (Exception e) {
            log.error("Failed to embed document {}", document.getId(), e);
            document.setStatus(DocumentStatus.FAILED);
            document.setFailureReason(e.getMessage());
        }

        document = documentRepository.save(document);
        return modelMapper.map(document, DocRespDTO.class);
    }

    @Override
    public List<DocRespDTO> getDocumentsByBotId(UUID botId, boolean succeeded) {
        botService.assertAuthorizedToBot(botId);

        List<Document> docs = documentRepository.findByBot_Id(botId);

        List<DocRespDTO> response = docs.stream()
                .map(doc -> modelMapper.map(doc, DocRespDTO.class))
                .toList();

        response = response.stream()
                .filter(resp -> resp.getStatus().equals(DocumentStatus.READY))
                .toList();

        return response;
    }

    @Override
    public Map<String, String> getDocumentById(UUID botId, UUID documentId) {
        botService.assertAuthorizedToBot(botId);

        List<DocumentChunk> chunks = documentChunkRepository.findAllByBot_IdAndDocument_IdOrderByChunkIndexAsc(botId, documentId);

        if (chunks.isEmpty()) {
            throw new ResourceNotFoundException("Document not found");
        }

        String content = chunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n"));

        return Map.of("content", content);
    }

    @Override
    public void deleteDocumentById(UUID botId, UUID documentId) {
        botService.assertAuthorizedToBot(botId);

        documentRepository.deleteById(documentId);
    }
}
