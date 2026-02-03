package com.example.moneo.controller;

import com.example.moneo.dto.DashboardDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.TransactionService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin
public class DashboardController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(transactionService.getDashboardSummary(user.getId()));
    }
}