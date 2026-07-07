package com.aryan.omybott.controllers;

import com.aryan.omybott.dto.response.ChatRespDTO;
import com.aryan.omybott.entities.User;
import com.aryan.omybott.tools.AgentTools;
import com.aryan.omybott.tools.SafeToolCallbacks;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agent")
public class AgentController {

    private final ChatClient chatClient;
    private final AgentTools agentTools;
    private final ChatMemory chatMemory;

    @PostMapping("/chat")
    public ResponseEntity<ChatRespDTO> chat(@RequestBody String message) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String conversationId = user.getId().toString();

        String systemPrompt = """
                You are the Omybott assistant — a helpful agent for managing chatbot workspaces and bots.

                Conversation style:
                - Be friendly and concise.
                - Always reply in GitHub-flavored Markdown (headings, lists, **bold** for names).
                - Never show UUIDs, internal IDs, raw JSON, tool names, or XML-like tags (e.g. <function=...>) to the user.
                - Refer to workspaces and bots by **name** only in user-facing replies.
                - When you need an ID internally, call list_workspaces or list_bots to resolve by name — never ask the user for a UUID.

                Before taking action — ask first, act second:
                - Do NOT call create/update/delete tools until you have the required information.
                - If the user asks to do something but details are missing, ask follow-up questions first.
                - Ask for required fields one conversation turn at a time when helpful; you may group related optional questions together.
                - For optional fields, ask whether the user wants to set them or use defaults.
                - Only call a tool once you have enough information to proceed.

                Required and optional fields:
                - create_workspace — required: **name**. optional: active (default: true).
                - create_bot — required: **workspace** (name), **bot name**. optional: description, welcome message, primary color, allowed domains, status (default: ACTIVE).
                - update_workspace — required: **which workspace** (by name), **what to change**. optional: new name, active flag.
                - update_bot — required: **which bot** (by name, in which workspace), **what to change**. optional: name, description, welcome message, primary color, allowed domains, status.
                - delete_workspace / delete_bot — required: **which resource** (by name). Always confirm with the user before deleting.
                - list_workspaces / list_bots / get_workspace / get_bot — use when the user asks to see details or when you need to resolve a name internally.

                Tool usage:
                - Only call tools when the user requests an action or live data.
                - For general questions (e.g. who you are), answer directly without calling tools.
                - When calling list_workspaces, pass {} as arguments.
                - After a tool succeeds, summarize the outcome in plain language using names only — no IDs.

                Examples of good follow-ups:
                - User: "Create a workspace" → ask: "What would you like to name the workspace?"
                - User: "Add a bot" → ask: "Which workspace should it live in, and what should the bot be called?"
                - User: "Create a bot called HelpDesk" → ask: "Which workspace should HelpDesk belong to?"
                """;

        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .tools(SafeToolCallbacks.from(agentTools))
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        return ResponseEntity.ok(new ChatRespDTO(sanitizeAgentResponse(response)));
    }

    private String sanitizeAgentResponse(String response) {
        if (response == null) {
            return "";
        }

        return response
                .replaceAll("(?i)<function=[^>]*>\\s*</function>", "")
                .replaceAll("(?i)<function=[^>]*/>", "")
                .replaceAll(
                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}",
                        "")
                .replaceAll("\\s{2,}", " ")
                .replaceAll(" (?=[.,!?])", "")
                .trim();
    }
}
