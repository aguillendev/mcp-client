package com.example.mcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Procesa mensajes de usuarios manteniendo historial de conversación
 * por número de teléfono (cada número es una sesión independiente).
 * El historial se guarda en memoria — se resetea al reiniciar la aplicación.
 */
@Service
public class OrchestrationService {

    private final ChatClient chatClient;

    // Clave: número de teléfono — Valor: historial completo de la conversación
    private final ConcurrentHashMap<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    public OrchestrationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Procesa un mensaje del usuario, manteniendo el historial de la conversación.
     *
     * @param phoneNumber El número de teléfono del usuario (actúa como ID de sesión)
     * @param message     El texto del mensaje del usuario
     * @return La respuesta generada por el LLM
     */
    public String processMessage(String phoneNumber, String message) {
        // Obtener o crear historial para este número
        List<Message> history = conversationHistory.computeIfAbsent(phoneNumber, k -> new ArrayList<>());

        // Agregar el mensaje del usuario al historial
        history.add(new UserMessage(message));

        // Llamar al LLM con el historial completo
        String response = chatClient.prompt()
                .messages(history)
                .call()
                .content();

        // Agregar la respuesta del AI al historial para el próximo turno
        history.add(new AssistantMessage(response));

        return response;
    }

    /**
     * Limpia el historial de conversación de un usuario.
     */
    public void clearHistory(String phoneNumber) {
        conversationHistory.remove(phoneNumber);
    }
}
