package com.example.moneo.controller;

import com.example.moneo.dto.SyncDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.SyncService;
import com.example.moneo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Tag(name = "Sinxronizasiya", description = "Local məlumatların serverlə sinxronizasiyası API-ləri")
public class SyncController {

    private final SyncService syncService;
    private final UserService userService;

    @Operation(
            summary = "Konflikt yoxlaması",
            description = "Local məlumat olub-olmamasına əsasən serverdə konflikt olub-olmadığını yoxlayır"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Konflikt vəziyyəti uğurla qaytarıldı"),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır")
    })
    @GetMapping("/check-conflict")
    public ResponseEntity<SyncDTO.ConflictResponse> checkConflict(
            @Parameter(description = "Local məlumat var?", example = "true", required = true)
            @RequestParam boolean hasLocalData
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(syncService.checkConflict(user.getId(), hasLocalData));
    }

    @Operation(
            summary = "Konflikti həll et",
            description = "keep_local və ya keep_server strategiyası ilə konflikti həll edir"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Konflikt həll edildi",
                    content = @Content(schema = @Schema(implementation = SyncDTO.SyncResponse.class))),
            @ApiResponse(responseCode = "400", description = "Yanlış strategiya"),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır")
    })
    @PostMapping("/resolve-conflict")
    public ResponseEntity<SyncDTO.SyncResponse> resolveConflict(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Konflikt həll etmə sorğusu (strategiya + local data)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SyncDTO.ResolveRequest.class))
            )
            SyncDTO.ResolveRequest request
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(syncService.resolveConflict(request, email));
    }

    @Operation(
            summary = "Məlumatları yüklə (bootstrap)",
            description = "Local məlumatları serverə yükləyir (accounts, categories, transactions + onboarding)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Məlumatlar uğurla sinxronizasiya edildi",
                    content = @Content(schema = @Schema(implementation = SyncDTO.SyncResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır")
    })
    @PostMapping("/bootstrap")
    public ResponseEntity<SyncDTO.SyncResponse> syncData(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Sinxronizasiya sorğusu (accounts, categories, transactions + onboarding)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SyncDTO.SyncRequest.class))
            )
            SyncDTO.SyncRequest request
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SyncDTO.SyncResponse response = syncService.syncData(request, email);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Server məlumatlarını al",
            description = "Serverdəki bütün məlumatları qaytarır: accounts, categories, transactions və onboarding"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Server məlumatları uğurla qaytarıldı",
                    content = @Content(schema = @Schema(implementation = SyncDTO.SyncDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token etibarsızdır")
    })
    @GetMapping("/data")
    public ResponseEntity<SyncDTO.SyncDataResponse> getSyncData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(new SyncDTO.SyncDataResponse());
    }
}