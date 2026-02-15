package com.example.moneo.controller;

import com.example.moneo.dto.SyncDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.SyncService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;
    private final UserService userService;

    @GetMapping("/check-conflict")
    public ResponseEntity<SyncDTO.ConflictResponse> checkConflict(@RequestParam boolean hasLocalData) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(syncService.checkConflict(user.getId(), hasLocalData));
    }

    @PostMapping("/resolve-conflict")
    public ResponseEntity<SyncDTO.SyncResponse> resolveConflict(@RequestBody SyncDTO.ResolveRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(syncService.resolveConflict(request, email));
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<SyncDTO.SyncResponse> syncData(@RequestBody SyncDTO.SyncRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SyncDTO.SyncResponse response = syncService.syncData(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    public ResponseEntity<SyncDTO.SyncResponse> getSyncData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        SyncDTO.SyncResponse response = syncService.syncData(new SyncDTO.SyncRequest(), email);
        return ResponseEntity.ok(response);
    }
}