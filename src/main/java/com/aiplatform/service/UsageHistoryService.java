package com.aiplatform.service;

import com.aiplatform.dto.DailyUsageResponse;
import com.aiplatform.repository.UsageLogRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageHistoryService {

    private final UsageLogRepository usageLogRepository;

    public List<DailyUsageResponse> getLast7Days(String userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return usageLogRepository.findByUserIdAndUsageDateBetween(userId, startDate, endDate)
                .stream()
                .map(r -> new DailyUsageResponse(r.getUsageDate(), r.getTokensUsed()))
                .collect(Collectors.toList());
    }
}
