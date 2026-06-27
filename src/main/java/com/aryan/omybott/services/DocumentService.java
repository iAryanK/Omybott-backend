package com.aryan.omybott.services;

import com.aryan.omybott.dto.request.DocReqDTO;
import com.aryan.omybott.dto.response.DocRespDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DocumentService {

    DocRespDTO createAndEmbedDocument(UUID botId, DocReqDTO docReqDTO, MultipartFile file) throws IOException;

    List<DocRespDTO> getDocumentsByBotId(UUID botId, boolean succeeded);

    Map<String, String> getDocumentById(UUID botId, UUID documentId);

    void deleteDocumentById(UUID botId, UUID documentId);
}
