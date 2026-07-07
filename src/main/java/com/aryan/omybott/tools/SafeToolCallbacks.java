package com.aryan.omybott.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.Arrays;

public final class SafeToolCallbacks {

    private SafeToolCallbacks() {
    }

    public static ToolCallback[] from(Object... toolObjects) {
        return Arrays.stream(ToolCallbacks.from(toolObjects))
                .map(SafeToolCallbacks::wrap)
                .toArray(ToolCallback[]::new);
    }

    private static ToolCallback wrap(ToolCallback delegate) {
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return delegate.getToolDefinition();
            }

            @Override
            public String call(String toolInput) {
                return delegate.call(normalizeToolInput(toolInput));
            }

            @Override
            public String call(String toolInput, ToolContext toolContext) {
                return delegate.call(normalizeToolInput(toolInput), toolContext);
            }
        };
    }

    private static String normalizeToolInput(String toolInput) {
        if (toolInput == null || toolInput.isBlank() || "null".equalsIgnoreCase(toolInput.trim())) {
            return "{}";
        }
        return toolInput;
    }
}
