package com.example.moneo.controller;

import com.example.moneo.dto.AuthDTO;
import com.example.moneo.service.AuthService;
import com.example.moneo.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDTO.RegisterRequest request) {
        authService.register(request);

        return ResponseEntity.ok(Map.of("message", "Qeydiyyat uğurla tamamlandı"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

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