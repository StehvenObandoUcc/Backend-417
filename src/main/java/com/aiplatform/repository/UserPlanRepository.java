package com.aiplatform.repository;

import com.aiplatform.domain.UserPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPlanRepository extends JpaRepository<UserPlan, String> {
}
