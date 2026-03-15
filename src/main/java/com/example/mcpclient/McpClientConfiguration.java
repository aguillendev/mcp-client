package com.example.mcpclient;


import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Configures the ChatClient with the MCP tools auto-discovered from the
 * remote MCP server (configured via spring.ai.mcp.client.sse in application.yml).
 *
 * The McpSyncClient beans are created automatically by the
 * spring-ai-starter-mcp-client auto-configuration.
 */
@Configuration
public class McpClientConfiguration {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ToolCallbackProvider mcpToolProvider) {
        return builder
            .defaultToolCallbacks(mcpToolProvider.getToolCallbacks())
            .build();
    }
}
