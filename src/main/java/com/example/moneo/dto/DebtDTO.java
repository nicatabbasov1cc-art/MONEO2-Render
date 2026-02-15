package com.example.moneo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DebtDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private BigDecimal totalAmount;
        private BigDecimal remainingAmount;
        private BigDecimal monthlyPayment;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Integer durationMonths;
        private String icon;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String name;
        private BigDecimal totalAmount;
        private BigDecimal remainingAmount;
        private BigDecimal monthlyPayment;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Integer durationMonths;
        private String icon;
    }
}