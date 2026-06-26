package com.aryan.omybott.services;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.request.WorkspaceReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.WorkspaceRespDTO;

import java.util.List;
import java.util.UUID;

public interface WorkspaceService {

    WorkspaceRespDTO createWorkspace(WorkspaceReqDTO name);

    List<WorkspaceRespDTO> getAllWorkspaces();

    WorkspaceRespDTO getWorkspaceById(UUID workspaceId);

    WorkspaceRespDTO patchWorkspaceById(UUID workspaceId, WorkspaceReqDTO workspaceReqDTO);

    void deleteWorkspaceById(UUID workspaceId);

    BotReqDTO createBotByWorkspaceId(UUID workspaceId, BotReqDTO botReqDTO);

    List<BotRespDTO> getAllBotsByWorkspaceId(UUID workspaceId);
}
