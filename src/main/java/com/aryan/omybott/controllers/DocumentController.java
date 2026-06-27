package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.DocReqDTO;
import com.aryan.omybott.dto.response.DocRespDTO;
import com.aryan.omybott.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bots/{botId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocRespDTO> createAndEmbedDocument(@PathVariable UUID botId,
                                                             @RequestPart("file") MultipartFile file,
                                                             @RequestPart(value = "metadata", required = false) DocReqDTO docReqDTO) throws IOException {
        DocRespDTO docRespDTO = documentService.createAndEmbedDocument(botId, docReqDTO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(docRespDTO);
    }

    @GetMapping
    public ResponseEntity<List<DocRespDTO>> getDocumentsByBotId(@PathVariable UUID botId,
                                                                @RequestParam boolean succeeded) {
        List<DocRespDTO> documents = documentService.getDocumentsByBotId(botId, succeeded);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, String>> getDocumentById(@PathVariable UUID botId,
                                                               @PathVariable UUID documentId) {
        Map<String, String> document = documentService.getDocumentById(botId, documentId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocumentById(@PathVariable UUID botId,
                                                               @PathVariable UUID documentId) {
        documentService.deleteDocumentById(botId, documentId);
        return ResponseEntity.noContent().build();
    }

}
