package com.example.moneo.controller;

import com.example.moneo.dto.DashboardDTO;
import com.example.moneo.dto.TransactionDTO;
import com.example.moneo.entity.TransactionEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.TransactionService;
import com.example.moneo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<DashboardDTO> addTransaction(@Valid @RequestBody TransactionDTO.CreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        TransactionEntity transaction = TransactionEntity.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .type(request.getType().toUpperCase())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .user(user)
                .build();

        transactionService.save(transaction);

        return ResponseEntity.ok(transactionService.getDashboardSummary(user.getId()));
    }
}