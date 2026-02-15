package com.example.moneo.controller;

import com.example.moneo.dto.UserDTO;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO.UserResponse> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserProfile(email));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<UserDTO.PreferencesDTO> updatePreferences(@RequestBody UserDTO.UpdatePreferencesRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.updatePreferences(email, request));
    }


    @DeleteMapping("/me/data")
    public ResponseEntity<Map<String, String>> clearUserData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.clearUserData(email);

        return ResponseEntity.ok(Map.of(
                "message", "Bütün istifadəçi məlumatları uğurla silindi",
                "status", "success"
        ));
    }
}