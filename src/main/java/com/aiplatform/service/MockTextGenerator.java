package com.aiplatform.service;

import com.aiplatform.dto.GenerationRequest;
import com.aiplatform.dto.GenerationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service("mockTextGenerator")
public class MockTextGenerator implements TextGenerationService {

    private static final List<String> RESPONSES = List.of(
            "La inteligencia artificial es una rama de la informática...",
            "El patrón Proxy nos permite controlar el acceso a un objeto...",
            "Spring Boot facilita la creación de aplicaciones stand-alone...",
            "Java 17 introdujo varias características como los records...",
            "Este es un texto simulado generado por un modelo ficticio..."
    );

    private final Random random = new Random();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        try {
            // Simulamos delay de la red o procesamiento del modelo
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String prompt = request.getPrompt();
        String text = RESPONSES.get(random.nextInt(RESPONSES.size()));

        // Estimación básica: caracteres / 4
        int promptTokens = prompt != null ? prompt.length() / 4 : 0;
        int responseTokens = text.length() / 4;
        int tokensUsed = promptTokens + responseTokens;

        return new GenerationResponse(text, tokensUsed, Instant.now());
    }
}
