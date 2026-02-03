package com.example.moneo.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private String aiSuggestion;
    private List<TransactionDTO.Response> lastTransactions;
}