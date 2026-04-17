package com.aiplatform.dto;

import com.aiplatform.domain.PlanType;
import lombok.Data;

@Data
public class UpgradeRequest {
    private String userId;
    private PlanType targetPlan;
}
