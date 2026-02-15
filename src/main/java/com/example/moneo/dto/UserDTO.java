package com.example.moneo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long userId;
        private String email;
        private PreferencesDTO preferences;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreferencesDTO {
        private boolean smartSuggestionsEnabled;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePreferencesRequest {
        private boolean smartSuggestionsEnabled;
    }
}