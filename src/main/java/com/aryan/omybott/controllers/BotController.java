package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.services.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bots")
public class BotController {

    private final BotService botService;

    @GetMapping("/{botId}")
    public ResponseEntity<BotRespDTO> getBotById(@PathVariable UUID botId) {
        BotRespDTO botRespDTO = botService.getBotById(botId);
        return ResponseEntity.ok(botRespDTO);
    }

    @PatchMapping("/{botId}")
    public ResponseEntity<BotRespDTO> patchBotById(@PathVariable UUID botId,
                                                   @RequestBody BotReqDTO botReqDTO) {
        BotRespDTO patchedBot = botService.patchBotById(botId, botReqDTO);
        return ResponseEntity.ok(patchedBot);
    }

    @DeleteMapping("/{botId}")
    public ResponseEntity<Void> deleteBotById(@PathVariable UUID botId) {
        botService.deleteBotById(botId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{botId}/playground/chat")
    public ResponseEntity<ChatRespDTO> chat(@PathVariable UUID botId,
                                            @RequestBody String message) {
        ChatRespDTO response = botService.getChatResponse(botId, message);
        return ResponseEntity.ok(response);
    }
}
