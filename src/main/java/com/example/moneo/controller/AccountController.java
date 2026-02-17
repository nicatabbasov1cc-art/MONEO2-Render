package com.example.moneo.controller;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Hesablar", description = "Hesab əməliyyatları API-ləri")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Yeni hesab yarat",
            description = "İlkin balans və valyuta göndərilərək yeni hesab yaradılır"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hesab uğurla yaradıldı",
                    content = @Content(schema = @Schema(implementation = AccountDTO.AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yanlış sorğu"),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır")
    })
    @PostMapping("/initialize")
    public ResponseEntity<AccountDTO.AccountResponse> createAccount(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Hesab yaratma sorğusu (balans və valyuta)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountDTO.CreateRequest.class))
            )
            AccountDTO.CreateRequest request
    ) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @Operation(
            summary = "İstifadəçinin hesablarını qaytar",
            description = "User ID-sinə əsasən bütün hesabları qaytarır"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hesab siyahısı uğurla qaytarıldı"),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır"),
            @ApiResponse(responseCode = "404", description = "İstifadəçi tapılmadı")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO.AccountResponse>> getAccountsByUserId(
            @Parameter(description = "İstifadəçi ID-si", example = "1", required = true)
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }
}