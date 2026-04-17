package com.aiplatform.controller;

import com.aiplatform.domain.PlanType;
import com.aiplatform.domain.UserPlan;
import com.aiplatform.dto.DailyUsageResponse;
import com.aiplatform.dto.QuotaStatusResponse;
import com.aiplatform.dto.UpgradeRequest;
import com.aiplatform.repository.UserPlanRepository;
import com.aiplatform.service.QuotaTracker;
import com.aiplatform.service.UsageHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quota")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class QuotaController {

    private final QuotaTracker quotaTracker;
    private final UsageHistoryService usageHistoryService;
    private final UserPlanRepository planRepository;

    @GetMapping("/status")
    public ResponseEntity<QuotaStatusResponse> getStatus(@RequestParam String userId) {
        return ResponseEntity.ok(quotaTracker.getStatus(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<DailyUsageResponse>> getHistory(@RequestParam String userId) {
        return ResponseEntity.ok(usageHistoryService.getLast7Days(userId));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradePlan(@RequestBody UpgradeRequest req) {
        UserPlan plan = planRepository.findById(req.getUserId()).orElseGet(() -> {
            UserPlan newPlan = new UserPlan();
            newPlan.setUserId(req.getUserId());
            newPlan.setLastResetDate(LocalDate.now());
            newPlan.setTokensUsedThisMonth(0);
            return newPlan;
        });

        plan.setPlanType(req.getTargetPlan());
        planRepository.save(plan);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "newPlan", req.getTargetPlan().name()
        ));
    }
}
