package com.example.moneo.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;

public class AuthDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Email tələb olunur")
        @Email(message = "Email formatı düzgün deyil")
        private String email;

        @NotBlank(message = "Şifrə tələb olunur")
        @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
        private String password;

        private String firstName;
        private String lastName;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String email;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email tələb olunur")
        @Email(message = "Email formatı düzgün deyil")
        private String email;

        @NotBlank(message = "Şifrə tələb olunur")
        private String password;
    }
}