package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.User;
import com.aryan.omybott.enums.BotStatus;
import com.aryan.omybott.exceptions.ResourceNotFoundException;
import com.aryan.omybott.exceptions.UnauthorizedException;
import com.aryan.omybott.repositories.BotRepository;
import com.aryan.omybott.services.BotService;
import com.aryan.omybott.vectorstore.DocumentChunkVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;
    private final ModelMapper modelMapper;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final DocumentChunkVectorStore documentChunkVectorStore;

    @Override
    public BotRespDTO getBotById(UUID botId) {
        assertAuthorizedToBot(botId);

        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new ResourceNotFoundException("Bot with id " + botId + " is not found"));

        return modelMapper.map(bot, BotRespDTO.class);
    }

    @Override
    @Transactional
    public BotRespDTO patchBotById(UUID botId, BotReqDTO botReqDTO) {
        Bot bot = assertAuthorizedToBot(botId);

        if (botReqDTO.getName() != null) {
            bot.setName(botReqDTO.getName());
            bot.setSlug(botReqDTO.getName().replace(" ", "_"));
        }
        if (botReqDTO.getDescription() != null) bot.setDescription(botReqDTO.getDescription());
        if (botReqDTO.getSlug() != null) bot.setSlug(botReqDTO.getSlug());
        if (botReqDTO.getWelcomeMessage() != null) bot.setWelcomeMessage(botReqDTO.getWelcomeMessage());
        if (botReqDTO.getPrimaryColor() != null) bot.setPrimaryColor(botReqDTO.getPrimaryColor());
        if (botReqDTO.getAllowedDomains() != null) bot.setAllowedDomains(botReqDTO.getAllowedDomains());
        if (botReqDTO.getStatus() != null) bot.setStatus(botReqDTO.getStatus());

        bot = botRepository.save(bot);

        return modelMapper.map(bot, BotRespDTO.class);
    }

    @Override
    public void deleteBotById(UUID botId) {
        assertAuthorizedToBot(botId);
        botRepository.deleteById(botId);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRespDTO getChatResponse(UUID botId, String message) {
        Bot bot = assertAuthorizedToBot(botId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String conversationId = botId + ":" + user.getId();
        return generateChatResponse(bot, conversationId, message);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRespDTO getPublicChatResponse(Bot bot, String conversationMemoryId, String message) {
        if (bot.getStatus() != BotStatus.ACTIVE) {
            throw new IllegalArgumentException("Bot is not active");
        }
        return generateChatResponse(bot, conversationMemoryId, message);
    }

    private ChatRespDTO generateChatResponse(Bot bot, String conversationMemoryId, String message) {
        String systemPrompt = String.format("""
                You are a support assistant. Your name is "%s".
                Answer questions using only the provided knowledge base context.
                If the answer is not in the context, say you do not know.
                """, bot.getName());

        documentChunkVectorStore.useBotScope(bot.getId());
        try {
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .advisors(
                            MessageChatMemoryAdvisor.builder(chatMemory).build(),
                            QuestionAnswerAdvisor.builder(documentChunkVectorStore)
                                    .searchRequest(SearchRequest.builder()
                                            .topK(4)
                                            .similarityThreshold(0.3)
                                            .build())
                                    .build()
                    )
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationMemoryId))
                    .call()
                    .content();

            return new ChatRespDTO(response);
        } finally {
            documentChunkVectorStore.clearBotScope();
        }
    }

    @Override
    public Bot assertAuthorizedToBot(UUID botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new ResourceNotFoundException("Bot with id " + botId + " is not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(bot.getWorkspace().getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You are not authorized to access bot id " + botId);
        }

        return bot;
    }
}
