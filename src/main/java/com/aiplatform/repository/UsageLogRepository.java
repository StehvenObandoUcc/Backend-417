package com.aiplatform.repository;

import com.aiplatform.domain.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsageLogRepository extends JpaRepository<UsageRecord, Long> {
    List<UsageRecord> findByUserIdAndUsageDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    Optional<UsageRecord> findByUserIdAndUsageDate(String userId, LocalDate usageDate);
}
