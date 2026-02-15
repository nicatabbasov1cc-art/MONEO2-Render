package com.example.moneo.controller;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.dto.TransactionDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.TransactionService;
import com.example.moneo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<TransactionDTO.Response> addTransaction(@Valid @RequestBody TransactionDTO.CreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(transactionService.createTransaction(request, user));
    }

    @GetMapping
    public ResponseEntity<AccountDTO.TransactionListResponse> getAllTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(transactionService.getTransactions(user.getId(), type, from, to, categoryIds, search, page, limit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO.Response> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO.CreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(transactionService.updateTransaction(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        transactionService.deleteTransaction(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}