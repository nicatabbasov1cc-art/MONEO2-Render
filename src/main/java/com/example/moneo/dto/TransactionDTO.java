package com.example.moneo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "Məbləğ tələb olunur")
        @DecimalMin(value = "0.01", message = "Məbləğ 0-dan böyük olmalıdır")
        private BigDecimal amount;

        @NotNull(message = "Kateqoriya ID-si tələb olunur")
        private Long categoryId;

        @NotBlank(message = "Növ tələb olunur")
        @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "Növ yalnız INCOME və ya EXPENSE ola bilər")
        private String type;

        private String note;

        @NotNull(message = "Tarix tələb olunur")
        private LocalDate date;

        @NotNull(message = "Hesab ID-si tələb olunur")
        private Long accountId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private BigDecimal amount;
        private String type;
        private String note;
        private LocalDate date;
        private Long categoryId;
        private String categoryName;
    }
}