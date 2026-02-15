package com.example.moneo.controller;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.TransactionService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/summary")
    public ResponseEntity<AccountDTO.DashboardResponse> getSummary() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(transactionService.getDashboardSummary(user.getId()));
    }
}