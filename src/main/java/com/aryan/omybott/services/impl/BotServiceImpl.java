package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.User;
import com.aryan.omybott.entities.Workspace;
import com.aryan.omybott.exceptions.ResourceNotFoundException;
import com.aryan.omybott.exceptions.UnauthorizedException;
import com.aryan.omybott.repositories.BotRepository;
import com.aryan.omybott.services.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    @Override
    public BotRespDTO getBotById(UUID botId) {
        assertAuthorizedToBot(botId);

        Bot bot = botRepository.findById(botId).
                orElseThrow(() -> new ResourceNotFoundException("Bot with id "+botId+" is not found"));

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

    private Bot assertAuthorizedToBot(UUID botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new ResourceNotFoundException("Bot with id "+botId+" is not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ( !(bot.getWorkspace().getOwner().getId().equals(user.getId())) ) {
            throw new UnauthorizedException("You are not authorized to access bot id "+botId);
        }

        return bot;
    }
}
