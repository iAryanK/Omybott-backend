package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.PublicChatReqDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatRespDTO> chat(@RequestHeader("X-Omybott-Key") String apiKey,
                                            @RequestHeader("Origin") String origin,
                                            @RequestBody PublicChatReqDTO publicChatReqDTO) {
        ChatRespDTO response = chatService.getChatResponse(apiKey, origin, publicChatReqDTO);
        return ResponseEntity.ok(response);
    }

}
