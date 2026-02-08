package com.example.moneo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sinxronizasiya əməliyyatının nəticəsini qaytaran model")
public class SyncResponseDTO {

    @Schema(description = "Əməliyyatın uğur statusu", example = "true")
    private boolean success;

    @Schema(description = "Sinxronizasiya edilən obyektlərin sayı (məsələn: categories, accounts)",
            example = "{\"categories\": 5, \"transactions\": 12}")
    private Map<String, Integer> syncedCount;

    @Schema(description = "Sinxronizasiya zamanı yaranan xətalar və ya konfliktlər",
            example = "[\"Category 'Work' already exists\"]")
    private List<String> conflicts;
}