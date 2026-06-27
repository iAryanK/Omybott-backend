package com.aryan.omybott.services;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.entities.Bot;

import java.util.UUID;

public interface BotService {
    BotRespDTO getBotById(UUID botId);

    BotRespDTO patchBotById(UUID botId, BotReqDTO botReqDTO);

    void deleteBotById(UUID botId);

    ChatRespDTO getChatResponse(UUID botId, String message);

    Bot assertAuthorizedToBot(UUID botId);

}
