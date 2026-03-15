package com.example.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OrchestrationService {

    private final ChatClient chatClient;

    public OrchestrationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Receives a message (e.g. from WhatsApp gateway),
     * queries the LLM using tools registered from the remote MCP server,
     * and returns the final response.
     */
    public String processMessage(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
