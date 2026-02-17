package com.example.moneo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Schema(description = "Sinxronizasiya əməliyyatları üçün DTO sinifləri")
@Data
public class SyncDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sinxronizasiya sorğusu")
    public static class SyncRequest {
        @Schema(description = "Hesab siyahısı (hər biri balans və valyuta ilə)")
        private List<AccountDTO.CreateRequest> accounts;

        @Schema(description = "Kateqoriya siyahısı")
        private List<CategoryDTO.CreateRequest> categories;

        @Schema(description = "Tranzaksiya siyahısı")
        private List<AccountDTO.CreateTransactionRequest> transactions;

        @Schema(description = "Onboarding zamanı seçilmiş kateqoriyalar")
        private List<String> onboardingSelectedCategories;

        @Schema(description = "Onboarding zamanı seçilmiş əsas məqsəd", example = "Pul yığmaq")
        private String onboardingPrimaryGoal;

        @Schema(description = "AI tövsiyələr aktiv olsun?", example = "true")
        private Boolean onboardingWantsAiSuggestions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Sinxronizasiya cavabı")
    public static class SyncResponse {
        @Schema(description = "Status (SUCCESS, SYNCED_WITH_CONFLICTS)", example = "SUCCESS")
        private String status;

        @Schema(description = "Sinxronizasiya edilən element sayı", example = "5")
        private int syncedCount;

        @Schema(description = "Konflikt sayı", example = "0")
        private int conflictCount;

        @Schema(description = "Mesajlar siyahısı")
        private List<String> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Konflikt yoxlama cavabı")
    public static class ConflictResponse {
        @Schema(description = "Serverdə məlumat var?", example = "true")
        private boolean serverDataExists;

        @Schema(description = "Konflikt var? (hasLocalData && serverDataExists)", example = "false")
        private boolean conflict;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Konflikt həll etmə sorğusu")
    public static class ResolveRequest {
        @Schema(description = "Strategiya (keep_local / keep_server)", example = "keep_local")
        private String strategy;

        @Schema(description = "Local məlumatlar (keep_local seçilərsə göndərilir)")
        private SyncRequest localData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "Server məlumatları cavabı (GET /sync/data)")
    public static class SyncDataResponse {
        @Schema(description = "Hesab siyahısı")
        private List<AccountDTO.AccountResponse> accounts;

        @Schema(description = "Kateqoriya siyahısı")
        private List<CategoryDTO.Response> categories;

        @Schema(description = "Tranzaksiya siyahısı")
        private List<AccountDTO.TransactionResponse> transactions;

        @Schema(description = "Onboarding məlumatları (seçilmiş kateqoriyalar)")
        private List<String> onboardingSelectedCategories;

        @Schema(description = "Onboarding əsas məqsəd", example = "Pul yığmaq")
        private String onboardingPrimaryGoal;

        @Schema(description = "AI tövsiyələr aktiv?", example = "true")
        private Boolean onboardingWantsAiSuggestions;
    }
}