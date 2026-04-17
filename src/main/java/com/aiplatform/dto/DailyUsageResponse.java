package com.aiplatform.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyUsageResponse {
    private LocalDate date;
    private int tokensUsed;
}
