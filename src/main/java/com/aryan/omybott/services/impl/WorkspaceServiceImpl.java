package com.aryan.omybott.services.impl;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.request.WorkspaceReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.WorkspaceRespDTO;
import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.entities.User;
import com.aryan.omybott.entities.Workspace;
import com.aryan.omybott.exceptions.ResourceNotFoundException;
import com.aryan.omybott.exceptions.UnauthorizedException;
import com.aryan.omybott.repositories.BotRepository;
import com.aryan.omybott.repositories.WorkspaceRepository;
import com.aryan.omybott.services.WorkspaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final BotRepository botRepository;
    private final ModelMapper modelMapper;

    @Override
    public WorkspaceRespDTO createWorkspace(WorkspaceReqDTO workspaceReqDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Workspace workspace = Workspace.builder()
                .name(workspaceReqDTO.getName())
                .slug(workspaceReqDTO.getName().replace(" ", "_").trim())
                .owner(user)
                .active(workspaceReqDTO.isActive())
                .build();

        workspace = workspaceRepository.save(workspace);

        return modelMapper.map(workspace, WorkspaceRespDTO.class);
    }

    @Override
    public List<WorkspaceRespDTO> getAllWorkspaces() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Workspace> workspaces = workspaceRepository.findByOwner_Id(user.getId());

        return workspaces.stream()
                .map(workspace -> modelMapper.map(workspace, WorkspaceRespDTO.class))
                .toList();
    }

    @Override
    public WorkspaceRespDTO getWorkspaceById(UUID workspaceId) {
        Workspace workspace = assertAuthorizedToWorkspace(workspaceId);
        return modelMapper.map(workspace, WorkspaceRespDTO.class);
    }

    @Override
    @Transactional
    public WorkspaceRespDTO patchWorkspaceById(UUID workspaceId, WorkspaceReqDTO workspaceReqDTO) {
        Workspace workspace = assertAuthorizedToWorkspace(workspaceId);

        // Update only non-null fields
        if (workspaceReqDTO.getName() != null) {
            workspace.setName(workspaceReqDTO.getName());
        }
        workspace.setActive(true);

        workspaceRepository.save(workspace);
        return modelMapper.map(workspace, WorkspaceRespDTO.class);
    }

    @Override
    public void deleteWorkspaceById(UUID workspaceId) {
        assertAuthorizedToWorkspace(workspaceId);
        workspaceRepository.deleteById(workspaceId);
    }

    @Override
    public BotReqDTO createBotByWorkspaceId(UUID workspaceId, BotReqDTO botReqDTO) {
        Workspace workspace = assertAuthorizedToWorkspace(workspaceId);

        Bot bot = modelMapper.map(botReqDTO, Bot.class);
        bot.setWorkspace(workspace);

        bot = botRepository.save(bot);

        return modelMapper.map(bot, BotReqDTO.class);
    }

    @Override
    public List<BotRespDTO> getAllBotsByWorkspaceId(UUID workspaceId) {
        assertAuthorizedToWorkspace(workspaceId);

        List<Bot> bots = botRepository.findByWorkspace_Id(workspaceId);
        return bots.stream()
                .map(bot -> modelMapper.map(bot, BotRespDTO.class))
                .toList();
    }

    private Workspace assertAuthorizedToWorkspace(UUID workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("workspace with id "+workspaceId+" is not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ( !(workspace.getOwner().getId().equals(user.getId())) ) {
            throw new UnauthorizedException("You are not authorized to access workspace id "+workspaceId);
        }

        return workspace;
    }

}
