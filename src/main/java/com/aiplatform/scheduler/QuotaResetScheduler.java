package com.aiplatform.scheduler;

import com.aiplatform.repository.UserPlanRepository;
import com.aiplatform.service.QuotaTracker;
import com.aiplatform.service.RateLimitingGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaResetScheduler {

    private final RateLimitingGuard rateLimitingGuard;
    private final QuotaTracker quotaTracker;
    private final UserPlanRepository planRepository;

    @Scheduled(fixedRate = 60000) // cada minuto
    public void resetRateWindows() {
        log.info("Executing Rate Limit Window Reset");
        rateLimitingGuard.resetWindows();
    }

    @Scheduled(cron = "0 0 0 1 * *") // A medianoche del primer día de cada mes
    public void resetMonthlyQuotas() {
        log.info("Executing Monthly Quota Reset");
        planRepository.findAll().forEach(plan -> {
            quotaTracker.resetMonthlyQuota(plan.getUserId());
        });
    }
}
