package com.aiplatform.dto;
import java.time.Instant;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationResponse {
    private String text;
    private int tokensUsed;
    private Instant timestamp;
}
