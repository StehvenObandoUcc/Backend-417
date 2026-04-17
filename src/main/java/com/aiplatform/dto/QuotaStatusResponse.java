package com.aiplatform.dto;
import com.aiplatform.domain.PlanType;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotaStatusResponse {
    private int used;
    private int remaining;
    private LocalDate resetDate;
    private PlanType plan;
}
