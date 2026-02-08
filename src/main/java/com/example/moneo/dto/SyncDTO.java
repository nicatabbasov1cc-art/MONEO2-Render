package com.example.moneo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class SyncDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SyncRequest {
        private List<AccountDTO.CreateRequest> accounts;
        private List<CategoryDTO.CreateRequest> categories;
        private List<AccountDTO.CreateTransactionRequest> transactions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SyncResponse {
        private String status;
        private int syncedCount;
        private int conflictCount;
        private List<String> messages;
    }
}