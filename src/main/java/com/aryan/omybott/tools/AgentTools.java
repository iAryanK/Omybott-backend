package com.aryan.omybott.tools;

import com.aryan.omybott.dto.request.BotReqDTO;
import com.aryan.omybott.dto.request.WorkspaceReqDTO;
import com.aryan.omybott.dto.response.BotRespDTO;
import com.aryan.omybott.dto.response.WorkspaceRespDTO;
import com.aryan.omybott.enums.BotStatus;
import com.aryan.omybott.services.BotService;
import com.aryan.omybott.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgentTools {

    private final WorkspaceService workspaceService;
    private final BotService botService;

    @Tool(
            name = "create_workspace",
            description = """
                    Create a new workspace. Only call when the user has provided a name.
                    Required: name. Optional: active (defaults to true).
                    """
    )
    public WorkspaceRespDTO createWorkspace(
            @ToolParam(description = "Display name of the workspace", required = true)
            String name,
            @ToolParam(description = "Whether the workspace is active (defaults to true)")
            Boolean active
    ) {
        WorkspaceReqDTO request = new WorkspaceReqDTO(name, active != null ? active : true);
        return workspaceService.createWorkspace(request);
    }

    @Tool(
            name = "list_workspaces",
            description = "List all workspaces owned by the authenticated user. Call with an empty JSON object {}."
    )
    public List<WorkspaceRespDTO> listWorkspaces(
            @ToolParam(description = "Optional. Always pass {}.", required = false)
            String unused
    ) {
        return workspaceService.getAllWorkspaces();
    }

    @Tool(
            name = "get_workspace",
            description = "Get workspace details by internal ID. Resolve the workspace name via list_workspaces first."
    )
    public WorkspaceRespDTO getWorkspace(
            @ToolParam(description = "Workspace internal ID (resolve from name via list_workspaces)", required = true)
            String workspaceId
    ) {
        return workspaceService.getWorkspaceById(parseUuid(workspaceId, "workspaceId"));
    }

    @Tool(
            name = "update_workspace",
            description = """
                    Update a workspace. Only call when you know which workspace and what to change.
                    Required: workspace internal ID (resolve from name via list_workspaces).
                    Optional: new name, active flag.
                    """
    )
    public WorkspaceRespDTO updateWorkspace(
            @ToolParam(description = "Workspace internal ID (resolve from name via list_workspaces)", required = true)
            String workspaceId,
            @ToolParam(description = "New workspace name")
            String name,
            @ToolParam(description = "Whether the workspace should be active")
            Boolean active
    ) {
        WorkspaceReqDTO request = new WorkspaceReqDTO();
        request.setName(name);
        if (active != null) {
            request.setActive(active);
        }
        return workspaceService.patchWorkspaceById(parseUuid(workspaceId, "workspaceId"), request);
    }

    @Tool(
            name = "delete_workspace",
            description = """
                    Permanently delete a workspace and all bots inside it.
                    Only call after the user has confirmed. Resolve workspace by name via list_workspaces first.
                    """
    )
    public String deleteWorkspace(
            @ToolParam(description = "Workspace internal ID (resolve from name via list_workspaces)", required = true)
            String workspaceId
    ) {
        WorkspaceRespDTO workspace = workspaceService.getWorkspaceById(parseUuid(workspaceId, "workspaceId"));
        workspaceService.deleteWorkspaceById(workspace.getId());
        return "Workspace \"" + workspace.getName() + "\" deleted successfully.";
    }

    @Tool(
            name = "create_bot",
            description = """
                    Create a bot inside a workspace. Only call when workspace and bot name are known.
                    Required: workspace internal ID (resolve from name via list_workspaces), bot name.
                    Optional: description, welcome message, primary color, allowed domains, status (default ACTIVE).
                    """
    )
    public BotRespDTO createBot(
            @ToolParam(description = "Workspace internal ID (resolve from name via list_workspaces)", required = true)
            String workspaceId,
            @ToolParam(description = "Bot display name", required = true)
            String name,
            @ToolParam(description = "Short description of what the bot does")
            String description,
            @ToolParam(description = "Welcome message shown when a user opens the chat")
            String welcomeMessage,
            @ToolParam(description = "Hex color for the bot widget, e.g. #FF0000")
            String primaryColor,
            @ToolParam(description = "Comma-separated list of allowed embed domains, e.g. example.com,localhost")
            String allowedDomains,
            @ToolParam(description = "Bot status: ACTIVE or INACTIVE (defaults to ACTIVE)")
            String status
    ) {
        BotReqDTO request = new BotReqDTO();
        request.setName(name);
        request.setDescription(description);
        request.setWelcomeMessage(welcomeMessage);
        if (primaryColor != null) {
            request.setPrimaryColor(primaryColor);
        }
        if (allowedDomains != null && !allowedDomains.isBlank()) {
            request.setAllowedDomains(parseAllowedDomains(allowedDomains));
        }
        if (status != null) {
            request.setStatus(parseBotStatus(status));
        }
        return workspaceService.createBotByWorkspaceId(parseUuid(workspaceId, "workspaceId"), request);
    }

    @Tool(
            name = "list_bots",
            description = "List all bots in a workspace. Resolve the workspace by name via list_workspaces first."
    )
    public List<BotRespDTO> listBots(
            @ToolParam(description = "Workspace internal ID (resolve from name via list_workspaces)", required = true)
            String workspaceId
    ) {
        return workspaceService.getAllBotsByWorkspaceId(parseUuid(workspaceId, "workspaceId"));
    }

    @Tool(
            name = "get_bot",
            description = "Get bot details by internal ID. Resolve bot and workspace names via list tools first."
    )
    public BotRespDTO getBot(
            @ToolParam(description = "Bot internal ID (resolve from name via list_bots)", required = true)
            String botId
    ) {
        return botService.getBotById(parseUuid(botId, "botId"));
    }

    @Tool(
            name = "update_bot",
            description = """
                    Update a bot. Only call when you know which bot and what to change.
                    Required: bot internal ID (resolve from name via list_bots).
                    Optional: name, description, welcome message, primary color, allowed domains, status.
                    """
    )
    public BotRespDTO updateBot(
            @ToolParam(description = "Bot internal ID (resolve from name via list_bots)", required = true)
            String botId,
            @ToolParam(description = "New bot display name")
            String name,
            @ToolParam(description = "New bot description")
            String description,
            @ToolParam(description = "New welcome message")
            String welcomeMessage,
            @ToolParam(description = "New hex color for the bot widget")
            String primaryColor,
            @ToolParam(description = "Comma-separated list of allowed embed domains")
            String allowedDomains,
            @ToolParam(description = "New bot status: ACTIVE or INACTIVE")
            String status
    ) {
        BotReqDTO request = new BotReqDTO();
        request.setName(name);
        request.setDescription(description);
        request.setWelcomeMessage(welcomeMessage);
        request.setPrimaryColor(primaryColor);
        if (allowedDomains != null) {
            request.setAllowedDomains(parseAllowedDomains(allowedDomains));
        }
        if (status != null) {
            request.setStatus(parseBotStatus(status));
        }
        return botService.patchBotById(parseUuid(botId, "botId"), request);
    }

    @Tool(
            name = "delete_bot",
            description = """
                    Permanently delete a bot. Only call after the user has confirmed.
                    Resolve bot by name via list_bots first.
                    """
    )
    public String deleteBot(
            @ToolParam(description = "Bot internal ID (resolve from name via list_bots)", required = true)
            String botId
    ) {
        BotRespDTO bot = botService.getBotById(parseUuid(botId, "botId"));
        botService.deleteBotById(bot.getId());
        return "Bot \"" + bot.getName() + "\" deleted successfully.";
    }

    private UUID parseUuid(String value, String fieldName) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": must be a valid UUID");
        }
    }

    private BotStatus parseBotStatus(String status) {
        try {
            return BotStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status: must be ACTIVE or INACTIVE");
        }
    }

    private Set<String> parseAllowedDomains(String allowedDomains) {
        return Arrays.stream(allowedDomains.split(","))
                .map(String::trim)
                .filter(domain -> !domain.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
