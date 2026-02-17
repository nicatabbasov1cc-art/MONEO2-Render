package com.example.moneo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Hesab əməliyyatları üçün DTO sinifləri")
public class AccountDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Hesab yaratma sorğusu")
    public static class CreateRequest {
        @Schema(description = "İlkin balans", example = "1000.00", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal balance;

        @Schema(description = "Valyuta", example = "AZN", requiredMode = Schema.RequiredMode.REQUIRED)
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Hesab məlumatları cavabı")
    public static class AccountResponse {
        @Schema(description = "Hesab ID-si", example = "1")
        private Long accountId;

        @Schema(description = "Hesab balansı", example = "1000.00")
        private BigDecimal balance;

        @Schema(description = "Valyuta", example = "AZN")
        private String currency;

        @Schema(description = "Son tranzaksiyalar")
        private List<TransactionResponse> transactions;

        @Schema(description = "Əməliyyatın uğur statusu", example = "true")
        private boolean success;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Tranzaksiya məlumatları")
    public static class TransactionResponse {
        @Schema(description = "Tranzaksiya ID-si", example = "10")
        private Long id;

        @Schema(description = "Tranzaksiya növü (INCOME/EXPENSE)", example = "EXPENSE")
        private String type;

        @Schema(description = "Məbləğ", example = "50.00")
        private BigDecimal amount;

        @Schema(description = "Təsvir", example = "Kafe")
        private String description;

        @Schema(description = "Kateqoriya adı", example = "Kafe")
        private String categoryName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pul köçürmə sorğusu")
    public static class TransferRequest {
        @Schema(description = "Mənbə hesab ID", example = "1")
        private Long fromAccountId;

        @Schema(description = "Hədəf hesab ID", example = "2")
        private Long toAccountId;

        @Schema(description = "Köçürüləcək məbləğ", example = "100.00")
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Dashboard məlumatları")
    public static class DashboardResponse {
        @Schema(description = "Cari balans", example = "950.00")
        private BigDecimal balance;



        @Schema(description = "Bu ay gəlirlər", example = "1000.00")
        private BigDecimal thisMonthIncome;

        @Schema(description = "Bu ay xərclər", example = "50.00")
        private BigDecimal thisMonthExpenses;

        @Schema(description = "Son tranzaksiyalar")
        private List<TransactionResponse> recentTransactions;

        @Schema(description = "Performans indeksi", example = "100.0")
        private double performanceIndex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Tranzaksiya siyahısı cavabı")
    public static class TransactionListResponse {
        @Schema(description = "Tranzaksiya siyahısı")
        private List<TransactionResponse> transactions;

        @Schema(description = "Səhifələmə məlumatları")
        private PaginationResponse pagination;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Səhifələmə məlumatları")
    public static class PaginationResponse {
        @Schema(description = "Cari səhifə", example = "0")
        private int currentPage;

        @Schema(description = "Ümumi səhifə sayı", example = "5")
        private int totalPages;

        @Schema(description = "Ümumi məlumat sayı", example = "42")
        private long totalCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tranzaksiya yaratma sorğusu")
    public static class CreateTransactionRequest {
        @Schema(description = "Tranzaksiya növü (INCOME/EXPENSE)", example = "EXPENSE")
        private String type;

        @Schema(description = "Məbləğ", example = "50.00")
        private BigDecimal amount;

        @Schema(description = "Kateqoriya ID-si", example = "5")
        private Long categoryId;

        @Schema(description = "Tarix", example = "2024-01-15")
        private LocalDate date;

        @Schema(description = "Qeyd", example = "Kafe")
        private String note;

        @Schema(description = "Hesab ID-si", example = "11")
        private Long accountId;
    }
}