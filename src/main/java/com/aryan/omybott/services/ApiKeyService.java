package com.aryan.omybott.services;

import com.aryan.omybott.dto.request.ApiKeyReqDTO;
import com.aryan.omybott.dto.response.ApiKeyRespDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ApiKeyService {

    Map<String, String> createApiKey(UUID botId, ApiKeyReqDTO apiKeyReqDTO);

    List<ApiKeyRespDTO> getApiKeys(UUID botId);

    void deleteApiKey(UUID botId, UUID apiKeyId);

}
