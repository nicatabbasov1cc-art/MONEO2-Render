package com.example.moneo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CategoryDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String icon;
        private String type;
        private boolean isDefault;
        private Integer transactionCount;
    }

    @Data
    public static class CreateRequest {
        private String name;
        private String icon;
        private String type;
    }
}