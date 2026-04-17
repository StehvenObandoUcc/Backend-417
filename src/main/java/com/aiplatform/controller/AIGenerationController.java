package com.aiplatform.controller;

import com.aiplatform.dto.GenerationRequest;
import com.aiplatform.dto.GenerationResponse;
import com.aiplatform.exception.QuotaExceededException;
import com.aiplatform.exception.RateLimitException;
import com.aiplatform.service.TextGenerationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // Para desarrollo. Ajustar en PROD para dominios permitidos.
public class AIGenerationController {

    private final TextGenerationService service;

    public AIGenerationController(@Qualifier("rateLimitingGuard") TextGenerationService service) {
        // Enlaza el controlador con el primer proxy de la cadena.
        this.service = service;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> handleGeneration(@RequestBody GenerationRequest req) {
        try {
            GenerationResponse response = service.generate(req);
            return ResponseEntity.ok(response);
            
        } catch (RateLimitException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "60")
                    .body(e.getMessage());
                    
        } catch (QuotaExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(e.getMessage());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando solicitud.");
        }
    }
}
