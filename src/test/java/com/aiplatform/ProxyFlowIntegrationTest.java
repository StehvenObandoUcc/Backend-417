package com.aiplatform;

import com.aiplatform.dto.GenerationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProxyFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullGenerationFlowWithRateLimitingAndQuota() throws Exception {
        String userId = "test-user-flow";

        GenerationRequest req = new GenerationRequest();
        req.setUserId(userId);
        req.setPrompt("Hola, IA simulada!");

        String jsonRequest = objectMapper.writeValueAsString(req);

        // 1. Verificar Estado de Cuota (FREE plan por defecto: 50_000 tokens)
        mockMvc.perform(get("/api/quota/status").param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan").value("FREE"))
                .andExpect(jsonPath("$.used").value(0))
                .andExpect(jsonPath("$.remaining").value(50000));

        // 2. Primera petición de generación => Debe pasar 200 OK
        mockMvc.perform(post("/api/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").exists())
                .andExpect(jsonPath("$.tokensUsed").exists());

        // 3. Verificar que los tokens fueron descontados en el tracker
        mockMvc.perform(get("/api/quota/status").param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.used").value(org.hamcrest.Matchers.greaterThan(0)));

        // 4. Probar Rate Limiting del proxy (El plan FREE permite 10 requests por minuto)
        // Consumimos las 9 requests restantes
        for (int i = 0; i < 9; i++) {
            mockMvc.perform(post("/api/ai/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isOk());
        }

        // El request 11 en el mismo minuto debería ser bloqueado por RateLimitingGuard Proxy
        mockMvc.perform(post("/api/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"));
        
        // 5. Upgrade de Plan a PRO vía API (60 req/min, 500k tokens)
        String upgradeReq = "{\"userId\": \"" + userId + "\", \"targetPlan\": \"PRO\"}";
        mockMvc.perform(post("/api/quota/upgrade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(upgradeReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newPlan").value("PRO"));

        // 6. Probar Rate Limiting nuevamente => Como el plan es PRO, el request 11 ahora SÍ debería pasar
        mockMvc.perform(post("/api/ai/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }
}
