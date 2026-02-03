package com.example.moneo.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {

    @Data
    public static class CreateRequest {
        @NotNull(message = "Məbləğ tələb olunur")
        @DecimalMin(value = "0.01", message = "Məbləğ 0-dan böyük olmalıdır")
        private BigDecimal amount;

        @NotBlank(message = "Kateqoriya tələb olunur")
        private String category;

        @NotBlank(message = "Növ tələb olunur")
        @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Növ yalnız INCOME və ya EXPENSE ola bilər")
        private String type;

        private String description;

        @NotNull(message = "Tarix tələb olunur")
        private LocalDate transactionDate;
    }

    @Data
    public static class Response {
        private Long id;
        private BigDecimal amount;
        private String category;
        private String type;
        private String description;
        private LocalDate transactionDate;
    }
}