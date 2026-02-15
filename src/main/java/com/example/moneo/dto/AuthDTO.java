package com.example.moneo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "EMAIL_INVALID")
        private String email;

        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, message = "PASSWORD_TOO_SHORT")
        private String password;

        @NotBlank(message = "REPASSWORD_REQUIRED")
        @Size(min = 8, message = "PASSWORD_TOO_SHORT")
        private String repassword; // Yeni sahə
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private Long userId;
        private String email;
        private boolean hasData;
        private boolean success;

    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "EMAIL_REQUIRED")
        @Email(message = "EMAIL_INVALID")
        private String email;

        @NotBlank(message = "PASSWORD_REQUIRED")
        private String password;
    }
}