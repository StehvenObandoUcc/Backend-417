package com.aiplatform.dto;

import lombok.Data;

@Data
public class GenerationRequest {
    private String userId;
    private String prompt;
}
