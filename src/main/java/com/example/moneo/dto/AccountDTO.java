package com.example.moneo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AccountDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private BigDecimal balance;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountResponse {
        private Long accountId;
        private BigDecimal balance;
        private String currency;
        private List<TransactionResponse> transactions;
        private boolean success;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse {
        private Long id;
        private String type;
        private BigDecimal amount;
        private String description;
        private String categoryName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        private Long fromAccountId;
        private Long toAccountId;
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardResponse {
        private BigDecimal balance;
        private String trend;
        private BigDecimal thisMonthIncome;
        private BigDecimal thisMonthExpenses;
        private List<TransactionResponse> recentTransactions;
        private double performanceIndex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionListResponse {
        private List<TransactionResponse> transactions;
        private PaginationResponse pagination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationResponse {
        private int currentPage;
        private int totalPages;
        private long totalCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateTransactionRequest {
        private String type;
        private BigDecimal amount;
        private Long categoryId;
        private LocalDate date;
        private String note;
        private Long accountId;
    }
}