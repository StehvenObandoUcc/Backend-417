package com.aiplatform.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class UserPlan {
    
    @Id
    @Column(nullable = false, unique = true)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType = PlanType.FREE;

    @Column(nullable = false)
    private int tokensUsedThisMonth = 0;

    @Column(nullable = false)
    private LocalDate lastResetDate = LocalDate.now().withDayOfMonth(1);
}
