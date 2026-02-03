package com.example.moneo.controller;

import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@CrossOrigin
public class OnboardingController {

    private final UserService userService;

    @PostMapping
    public String saveOnboarding(@RequestBody OnboardingRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        if (user == null) return "ERROR: User not found";

        user.setPrimaryGoal(request.getPrimaryGoal());
        user.setSelectedCategories(request.getSelectedCategories());
        user.setWantsAiSuggestions(request.getWantsAiSuggestions());

        userService.save(user);

        return "Onboarding saved successfully for: " + email;
    }

    @Data
    static class OnboardingRequest {
        private String primaryGoal;
        private List<String> selectedCategories;
        private Boolean wantsAiSuggestions;
    }
}