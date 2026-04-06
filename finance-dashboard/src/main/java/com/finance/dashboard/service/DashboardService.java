package com.finance.dashboard.service;

import com.finance.dashboard.dto.response.DashboardSummaryResponse;
import com.finance.dashboard.dto.response.TransactionResponse;
import com.finance.dashboard.enums.TransactionType;
import com.finance.dashboard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome   = transactionRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance    = totalIncome.subtract(totalExpenses);
        Map<String, BigDecimal> categoryTotals = buildCategoryTotals();
        List<DashboardSummaryResponse.MonthlyTrend> trends = buildMonthlyTrends();
        List<TransactionResponse> recentActivity = transactionRepository.findRecentActivity()
                .stream()
                .map(TransactionResponse::new)
                .toList();
        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .monthlyTrends(trends)
                .recentActivity(recentActivity)
                .build();
    }

    private Map<String, BigDecimal> buildCategoryTotals() {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] row : transactionRepository.sumByCategory()) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }

    private List<DashboardSummaryResponse.MonthlyTrend> buildMonthlyTrends() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return transactionRepository.monthlyTrends(sixMonthsAgo)
                .stream()
                .map(row -> DashboardSummaryResponse.MonthlyTrend.builder()
                        .month((Integer) row[0])
                        .year((Integer) row[1])
                        .type(row[2].toString())
                        .total((BigDecimal) row[3])
                        .build())
                .toList();
    }
}
