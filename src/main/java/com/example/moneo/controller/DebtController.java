package com.example.moneo.controller;

import com.example.moneo.dto.DebtDTO;
import com.example.moneo.entity.DebtEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.DebtService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<DebtDTO.Response> createDebt(@RequestBody DebtDTO.Request request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        // Criteria: remainingAmount yoxdursa, totalAmount-a bərabər edilir
        var remainingAmount = (request.getRemainingAmount() != null)
                ? request.getRemainingAmount()
                : request.getTotalAmount();

        DebtEntity debt = DebtEntity.builder()
                .name(request.getName())
                .totalAmount(request.getTotalAmount())
                .remainingAmount(remainingAmount)
                .monthlyPayment(request.getMonthlyPayment())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .durationMonths(request.getDurationMonths())
                .icon(request.getIcon())
                .user(user)
                .build();

        return ResponseEntity.ok(convertToDTO(debtService.save(debt)));
    }

    @GetMapping
    public ResponseEntity<List<DebtDTO.Response>> getUserDebts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        return ResponseEntity.ok(debtService.getUserDebts(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDebt(@PathVariable Long id, @RequestBody DebtDTO.Request request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return debtService.findById(id).map(debt -> {
            if (!debt.getUser().getEmail().equals(email)) {
                return ResponseEntity.status(403).body("Bu borcu redaktə etmək icazəniz yoxdur!");
            }

            debt.setName(request.getName());
            debt.setTotalAmount(request.getTotalAmount());
            debt.setRemainingAmount(request.getRemainingAmount());
            debt.setMonthlyPayment(request.getMonthlyPayment());
            debt.setStartDate(request.getStartDate());
            debt.setDueDate(request.getDueDate());
            debt.setDurationMonths(request.getDurationMonths());
            debt.setIcon(request.getIcon());

            return ResponseEntity.ok(convertToDTO(debtService.save(debt)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDebt(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return debtService.findById(id).map(debt -> {
            if (!debt.getUser().getEmail().equals(email)) {
                return ResponseEntity.status(403).body("Bu borcu silmək icazəniz yoxdur!");
            }
            debtService.deleteById(id);
            return ResponseEntity.ok().body("Borc silindi. ID: " + id);
        }).orElse(ResponseEntity.notFound().build());
    }

    private DebtDTO.Response convertToDTO(DebtEntity debt) {
        return DebtDTO.Response.builder()
                .id(debt.getId())
                .name(debt.getName())
                .totalAmount(debt.getTotalAmount())
                .remainingAmount(debt.getRemainingAmount())
                .monthlyPayment(debt.getMonthlyPayment())
                .startDate(debt.getStartDate())
                .dueDate(debt.getDueDate())
                .durationMonths(debt.getDurationMonths())
                .icon(debt.getIcon())
                .createdAt(debt.getCreatedAt())
                .build();
    }
}