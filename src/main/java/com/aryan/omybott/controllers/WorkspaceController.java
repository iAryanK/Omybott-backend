package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.request.WorkspaceReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.WorkspaceRespDTO;
import com.aryan.omybott.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceRespDTO> createWorkspace(@RequestBody WorkspaceReqDTO workspaceReqDTO) {
        WorkspaceRespDTO workspaceRespDTO = workspaceService.createWorkspace(workspaceReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceRespDTO);
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceRespDTO>> getAllWorkspaces() {
        List<WorkspaceRespDTO> workspaces = workspaceService.getAllWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceRespDTO> getWorkspaceById(@RequestBody UUID workspaceId) {
        WorkspaceRespDTO workspaceRespDTO = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspaceRespDTO);
    }

    @PatchMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceRespDTO> patchWorkspaceById(@PathVariable UUID workspaceId,
                                                          @RequestBody WorkspaceReqDTO workspaceReqDTO) {
        WorkspaceRespDTO workspaceRespDTO = workspaceService.patchWorkspaceById(workspaceId, workspaceReqDTO);
        return ResponseEntity.ok(workspaceRespDTO);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspaceById(@PathVariable UUID workspaceId) {
        workspaceService.deleteWorkspaceById(workspaceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workspaceId}/bots")
    public ResponseEntity<BotReqDTO> createBotByWorkspaceId(@PathVariable UUID workspaceId,
                                                            @RequestBody BotReqDTO botReqDTO) {
        BotReqDTO createdBot = workspaceService.createBotByWorkspaceId(workspaceId, botReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBot);
    }

    @GetMapping("/{workspaceId}/bots")
    public ResponseEntity<List<BotRespDTO>> getAllBotsByWorkspaceId(@PathVariable UUID workspaceId) {
        List<BotRespDTO> bots = workspaceService.getAllBotsByWorkspaceId(workspaceId);
        return ResponseEntity.ok(bots);
    }
}
