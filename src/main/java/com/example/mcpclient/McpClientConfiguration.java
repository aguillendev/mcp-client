package com.example.mcpclient;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

@Configuration
public class McpClientConfiguration {

    @Value("${playmatch.mcp.server-url}")
    private String serverUrl;

    @Bean(initMethod = "initialize", destroyMethod = "close")
    public McpSyncClient mcpClient() {
        var transport = new HttpClientSseClientTransport(serverUrl);
        // Note: Using SyncClient to take advantage of virtual threads
        return McpClient.sync(transport).build();
    }

    @Bean
    public ToolCallbackProvider mcpToolProvider(McpSyncClient mcpClient) {
        return new SyncMcpToolCallbackProvider(mcpClient);
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpToolProvider) {
        return builder
            .defaultTools(mcpToolProvider.getToolCallbacks())
            .build();
    }
}
