package com.example.moneo.controller;

import com.example.moneo.dto.SyncDTO;
import com.example.moneo.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@CrossOrigin
public class SyncController {

    private final SyncService syncService;

    @PostMapping
    public ResponseEntity<SyncDTO.SyncResponse> syncData(@RequestBody SyncDTO.SyncRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        SyncDTO.SyncResponse response = syncService.syncData(request, email);

        return ResponseEntity.ok(response);
    }
}