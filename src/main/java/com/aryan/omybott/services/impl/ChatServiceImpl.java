package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.PublicChatReqDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.entities.ApiKey;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.ChatMessage;
import com.aryan.omybott.entities.Conversation;
import com.aryan.omybott.enums.MessageRole;
import com.aryan.omybott.exceptions.ResourceNotFoundException;
import com.aryan.omybott.exceptions.UnauthorizedException;
import com.aryan.omybott.repositories.ChatMessageRepository;
import com.aryan.omybott.repositories.ConversationRepository;
import com.aryan.omybott.services.ApiKeyService;
import com.aryan.omybott.services.BotService;
import com.aryan.omybott.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ApiKeyService apiKeyService;
    private final BotService botService;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ChatRespDTO getChatResponse(String apiKey, String origin, PublicChatReqDTO publicChatReqDTO) {
        if (publicChatReqDTO.getMessage() == null || publicChatReqDTO.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        ApiKey validatedKey = apiKeyService.validateAndGetApiKey(apiKey);
        Bot bot = validatedKey.getBot();

        validateOrigin(bot, origin);

        Conversation conversation = resolveConversation(bot, origin, publicChatReqDTO.getConversationId());

        saveMessage(conversation, MessageRole.USER, publicChatReqDTO.getMessage());

        String conversationMemoryId = bot.getId() + ":" + conversation.getId();
        ChatRespDTO response = botService.getPublicChatResponse(bot, conversationMemoryId, publicChatReqDTO.getMessage());

        saveMessage(conversation, MessageRole.ASSISTANT, response.getResponse());

        conversation.setLastMessageAt(Instant.now());
        conversationRepository.save(conversation);

        response.setConversationId(conversation.getId());
        return response;
    }

    private Conversation resolveConversation(Bot bot, String origin, UUID conversationId) {
        if (conversationId == null) {
            Conversation conversation = new Conversation();
            conversation.setBot(bot);
            conversation.setVisitorId(UUID.randomUUID().toString());
            conversation.setOrigin(origin);
            conversation.setPlayground(false);
            conversation.setLastMessageAt(Instant.now());
            return conversationRepository.save(conversation);
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation with id " + conversationId + " is not found"));

        if (!conversation.getBot().getId().equals(bot.getId())) {
            throw new UnauthorizedException("Conversation does not belong to this bot");
        }

        return conversation;
    }

    private void validateOrigin(Bot bot, String origin) {
        if (origin == null || origin.isBlank()) {
            throw new UnauthorizedException("Missing Origin header");
        }

        Set<String> allowedDomains = bot.getAllowedDomains();
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            throw new UnauthorizedException("No allowed domains configured for this bot");
        }

        String originHost = normalizeDomain(origin);
        boolean allowed = allowedDomains.stream()
                .map(this::normalizeDomain)
                .anyMatch(domain -> originHost.equals(domain));

        if (!allowed) {
            throw new UnauthorizedException("Origin is not allowed for this bot");
        }
    }

    private String normalizeDomain(String value) {
        String normalized = value.trim().toLowerCase();
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            try {
                normalized = URI.create(normalized).getHost();
            } catch (IllegalArgumentException ignored) {
                normalized = normalized.replaceFirst("^https?://", "");
            }
        }
        return normalized.replaceAll("/$", "");
    }

    private void saveMessage(Conversation conversation, MessageRole role, String content) {
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        message.setAnsweredFromKnowledgeBase(true);
        chatMessageRepository.save(message);
    }

}
