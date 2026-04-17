package com.aiplatform.domain;

public enum PlanType {
    FREE(10, 50_000),
    PRO(60, 500_000),
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE);

    public final int requestsPerMinute;
    public final int monthlyTokens;

    PlanType(int requestsPerMinute, int monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }
}
