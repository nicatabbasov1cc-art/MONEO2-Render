package com.example.moneo.controller;

import com.example.moneo.dto.AuthDTO;
import com.example.moneo.service.AuthService;
import com.example.moneo.service.RateLimitService;
import com.example.moneo.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Qeydiyyat, giriş və token yeniləmə API-ləri")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService;
    private final RateLimitService rateLimitService;

    @Operation(summary = "Qeydiyyat", description = "Yeni istifadəçi qeydiyyatı")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        if (!rateLimitService.tryConsumeRegister(request.getEmail())) {
            return ResponseEntity.status(429).body(Map.of(
                    "error", "TOO_MANY_REQUESTS",
                    "message", "Çox sayda qeydiyyat cəhdi. Bir az gözləyin."
            ));
        }
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Qeydiyyat uğurla tamamlandı"));
    }

    @Operation(summary = "Giriş", description = "Email və şifrə ilə giriş. Cavabda access token, refresh token və expiresIn qaytarılır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Giriş uğurlu",
                    content = @Content(schema = @Schema(implementation = AuthDTO.AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Yanlış email və ya şifrə"),
            @ApiResponse(responseCode = "429", description = "Çox sayda sorğu")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        if (!rateLimitService.tryConsumeLogin(request.getEmail())) {
            return ResponseEntity.status(429).body(Map.of(
                    "error", "TOO_MANY_REQUESTS",
                    "message", "Çox sayda giriş cəhdi. Bir az gözləyin."
            ));
        }
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Token yenilə",
            description = "Refresh token ilə yeni access token + refresh token al. Authorization header tələb olunmur."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token uğurla yeniləndi",
                    content = @Content(schema = @Schema(implementation = AuthDTO.AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token etibarsız və ya müddəti bitib")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody AuthDTO.RefreshRequest request) {
        try {
            return ResponseEntity.ok(authService.refresh(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "Giriş qadağandır, sessiyanı yeniləyib yenidən cəhd edin."
            ));
        }
    }

    @Operation(summary = "Çıxış", description = "Token qara siyahıya alınır")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            blacklistService.blacklistToken(authHeader);
            return ResponseEntity.ok(Map.of(
                    "message", "Uğurla çıxış edildi",
                    "success", "true"
            ));
        }
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Token tapılmadı",
                "success", "false"
        ));
    }
}