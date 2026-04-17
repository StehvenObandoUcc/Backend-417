package com.aiplatform.service;

import com.aiplatform.domain.UsageRecord;
import com.aiplatform.domain.UserPlan;
import com.aiplatform.dto.QuotaStatusResponse;
import com.aiplatform.repository.UsageLogRepository;
import com.aiplatform.repository.UserPlanRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuotaTracker {

    private final UserPlanRepository planRepository;
    private final UsageLogRepository usageLogRepository;

    public QuotaStatusResponse getStatus(String userId) {
        UserPlan plan = planRepository.findById(userId)
                .orElseGet(() -> createDefaultPlan(userId));
        
        int remaining = plan.getPlanType().monthlyTokens - plan.getTokensUsedThisMonth();
        if (remaining < 0) remaining = 0;

        return new QuotaStatusResponse(
                plan.getTokensUsedThisMonth(),
                remaining,
                plan.getLastResetDate().plusMonths(1),
                plan.getPlanType()
        );
    }

    @Transactional
    public void deduct(String userId, int tokens) {
        UserPlan plan = planRepository.findById(userId)
                .orElseGet(() -> createDefaultPlan(userId));
        
        plan.setTokensUsedThisMonth(plan.getTokensUsedThisMonth() + tokens);
        planRepository.save(plan);

        UsageRecord log = usageLogRepository.findByUserIdAndUsageDate(userId, LocalDate.now())
                .orElseGet(() -> {
                    UsageRecord newLog = new UsageRecord();
                    newLog.setUserId(userId);
                    newLog.setUsageDate(LocalDate.now());
                    newLog.setTokensUsed(0);
                    return newLog;
                });
        
        log.setTokensUsed(log.getTokensUsed() + tokens);
        usageLogRepository.save(log);
    }

    @Transactional
    public void resetMonthlyQuota(String userId) {
        planRepository.findById(userId).ifPresent(plan -> {
            plan.setTokensUsedThisMonth(0);
            plan.setLastResetDate(LocalDate.now());
            planRepository.save(plan);
        });
    }

    private UserPlan createDefaultPlan(String userId) {
        UserPlan plan = new UserPlan();
        plan.setUserId(userId);
        return planRepository.save(plan);
    }
}
