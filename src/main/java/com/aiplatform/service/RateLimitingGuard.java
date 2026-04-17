package com.aiplatform.service;

import com.aiplatform.domain.UserPlan;
import com.aiplatform.dto.GenerationRequest;
import com.aiplatform.dto.GenerationResponse;
import com.aiplatform.exception.RateLimitException;
import com.aiplatform.repository.UserPlanRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service("rateLimitingGuard")
public class RateLimitingGuard implements TextGenerationService {

    private final TextGenerationService next;
    private final UserPlanRepository planRepository;
    
    // ConcurrentHashMap para evitar problemas entre threads
    private final Map<String, AtomicInteger> activeWindows = new ConcurrentHashMap<>();

    public RateLimitingGuard(
            @Qualifier("quotaBillingGuard") TextGenerationService next,
            UserPlanRepository planRepository) {
        this.next = next;
        this.planRepository = planRepository;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        String userId = request.getUserId();
        int limit = planRepository.findById(userId)
                .map(plan -> plan.getPlanType().requestsPerMinute)
                .orElse(10); // Límite por defecto (FREE)

        activeWindows.putIfAbsent(userId, new AtomicInteger(0));
        AtomicInteger count = activeWindows.get(userId);

        if (count.get() >= limit) {
            throw new RateLimitException("Demasiadas requests en el mismo minuto. Límite: " + limit);
        }

        // Si pasa, incrementamos el contador
        count.incrementAndGet();

        // Delegamos al siguiente paso en la cadena (QuotaBillingGuard)
        return next.generate(request);
    }

    public void resetWindows() {
        activeWindows.clear();
    }
}
