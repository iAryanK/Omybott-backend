package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.ApiKeyReqDTO;
import com.aryan.omybott.dto.response.ApiKeyRespDTO;
import com.aryan.omybott.services.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bots/{botId}/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createApiKey(@PathVariable UUID botId,
                                                            @RequestBody ApiKeyReqDTO apiKeyReqDTO) {
        Map<String, String> apiKey = apiKeyService.createApiKey(botId, apiKeyReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKey);
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyRespDTO>> getApiKeys(@PathVariable UUID botId) {
        List<ApiKeyRespDTO> apiKeys = apiKeyService.getApiKeys(botId);
        return ResponseEntity.ok(apiKeys);
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> deleteApiKey(@PathVariable UUID botId,
                                                            @PathVariable UUID apiKeyId) {
        apiKeyService.deleteApiKey(botId, apiKeyId);
        return ResponseEntity.noContent().build();
    }

}
