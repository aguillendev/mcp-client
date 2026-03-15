package com.example.mcpclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OrchestrationService orchestrationService;

    public ChatController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    /**
     * Recibe un mensaje de texto junto con el número de teléfono del usuario,
     * lo procesa con el LLM manteniendo el historial de conversación,
     * y devuelve la respuesta.
     */
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String response = orchestrationService.processMessage(request.phoneNumber(), request.message());
        return ResponseEntity.ok(response);
    }

    public record ChatRequest(String phoneNumber, String message) {}
}
