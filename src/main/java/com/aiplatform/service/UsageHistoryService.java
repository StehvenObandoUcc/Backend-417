package com.aiplatform.service;

import com.aiplatform.dto.DailyUsageResponse;
import com.aiplatform.repository.UsageLogRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageHistoryService {

    private final UsageLogRepository usageLogRepository;

    public List<DailyUsageResponse> getLast7Days(String userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        // Fetch existent logs
        Map<LocalDate, Integer> usageMap = usageLogRepository.findByUserIdAndUsageDateBetween(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(r -> r.getUsageDate(), r -> r.getTokensUsed()));

        // Ensure 7 days are always returned
        List<DailyUsageResponse> fullHistory = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LocalDate current = startDate.plusDays(i);
            fullHistory.add(new DailyUsageResponse(current, usageMap.getOrDefault(current, 0)));
        }

        return fullHistory;
    }
}
