package com.aryan.omybott.services;

import com.aryan.omybott.dto.request.PublicChatReqDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;

public interface ChatService {
    ChatRespDTO getChatResponse(String apiKey, String origin, PublicChatReqDTO publicChatReqDTO);
}
