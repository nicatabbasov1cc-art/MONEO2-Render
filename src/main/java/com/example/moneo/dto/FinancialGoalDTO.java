package com.example.moneo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialGoalDTO {

    @Data
    public static class CreateRequest {
        private String email;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private String goalType;
        private LocalDate targetDate;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private String goalType;
        private LocalDate targetDate;
        private BigDecimal progressPercentage;
    }
}