package com.aiplatform.service;

import com.aiplatform.domain.UserPlan;
import com.aiplatform.dto.GenerationRequest;
import com.aiplatform.dto.GenerationResponse;
import com.aiplatform.exception.QuotaExceededException;
import com.aiplatform.repository.UserPlanRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("quotaBillingGuard")
public class QuotaBillingGuard implements TextGenerationService {

    private final TextGenerationService next;
    private final QuotaTracker quotaTracker;
    private final UserPlanRepository planRepository;

    public QuotaBillingGuard(
            @Qualifier("mockTextGenerator") TextGenerationService next,
            QuotaTracker quotaTracker,
            UserPlanRepository planRepository) {
        this.next = next;
        this.quotaTracker = quotaTracker;
        this.planRepository = planRepository;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        String userId = request.getUserId();
        Optional<UserPlan> planOpt = planRepository.findById(userId);

        if (planOpt.isPresent()) {
            UserPlan plan = planOpt.get();
            int remaining = plan.getPlanType().monthlyTokens - plan.getTokensUsedThisMonth();
            if (remaining <= 0) {
                throw new QuotaExceededException("Has superado tu cuota mensual de tokens.");
            }
        }

        // Si tenemos cuota, llamamos al siguiente eslabón (MockTextGenerator)
        GenerationResponse response = next.generate(request);

        // Descontamos los tokens que se consumieron
        if (response != null) {
            quotaTracker.deduct(userId, response.getTokensUsed());
        }

        return response;
    }
}
