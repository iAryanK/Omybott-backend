package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.ApiKeyReqDTO;
import com.aryan.omybott.dto.response.ApiKeyRespDTO;
import com.aryan.omybott.entities.ApiKey;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.repositories.ApiKeyRepository;
import com.aryan.omybott.services.ApiKeyService;
import com.aryan.omybott.services.BotService;
import com.aryan.omybott.util.RandomKeyGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final BotService botSerivce;
    private final ApiKeyRepository apiKeyRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Map<String, String> createApiKey(UUID botId, ApiKeyReqDTO apiKeyReqDTO) {
        Bot bot = botSerivce.assertAuthorizedToBot(botId);

        String keyId = "omy_"+"live"+"_"+ RandomKeyGenerator.randomBase64(24);

        ApiKey apiKey = modelMapper.map(apiKeyReqDTO, ApiKey.class);
        apiKey.setBot(bot);
        apiKey.setHashedKey(passwordEncoder.encode(keyId));

        apiKey  = apiKeyRepository.save(apiKey);
        return Map.of("apiKey", keyId);
    }

    @Override
    public List<ApiKeyRespDTO> getApiKeys(UUID botId) {
        botSerivce.assertAuthorizedToBot(botId);

        List<ApiKey> apiKeys = apiKeyRepository.findByBot_id(botId);

        return apiKeys.stream()
                .map(apiKey -> modelMapper.map(apiKey, ApiKeyRespDTO.class))
                .toList();
    }

    @Override
    public void deleteApiKey(UUID botId, UUID apiKeyId) {
        botSerivce.assertAuthorizedToBot(botId);

        apiKeyRepository.deleteById(apiKeyId);
    }
}
