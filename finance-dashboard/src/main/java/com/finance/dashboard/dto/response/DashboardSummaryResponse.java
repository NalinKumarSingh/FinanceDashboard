package com.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DashboardSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryTotals;
    private List<MonthlyTrend> monthlyTrends;
    private List<TransactionResponse> recentActivity;

    @Getter
    @Builder
    public static class MonthlyTrend {
        private int month;
        private int year;
        private String type;
        private BigDecimal total;
    }
}
