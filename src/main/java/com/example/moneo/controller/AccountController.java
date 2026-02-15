package com.example.moneo.controller;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/initialize")
    public ResponseEntity<AccountDTO.AccountResponse> createAccount(@Valid @RequestBody AccountDTO.CreateRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO.AccountResponse>> getAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }
}