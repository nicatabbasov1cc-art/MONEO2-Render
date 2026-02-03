package com.example.moneo.controller;

import com.example.moneo.dto.FinancialGoalDTO;
import com.example.moneo.entity.FinancialGoalEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.FinancialGoalService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin
public class FinancialGoalController {

    private final FinancialGoalService goalService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody FinancialGoalDTO.CreateRequest request) {
        UserEntity user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest().body("ERROR: User not found with email: " + request.getEmail());
        }

        FinancialGoalEntity goal = FinancialGoalEntity.builder()
                .name(request.getName())
                .targetAmount(request.getTargetAmount())
                .currentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO)
                .goalType(request.getGoalType())
                .targetDate(request.getTargetDate())
                .user(user)
                .build();

        return ResponseEntity.ok(convertToDTO(goalService.save(goal)));
    }

    @GetMapping
    public ResponseEntity<List<FinancialGoalDTO.Response>> getUserGoals(@RequestParam String email) {
        UserEntity user = userService.findByEmail(email);
        if (user == null) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(goalService.getUserGoals(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private FinancialGoalDTO.Response convertToDTO(FinancialGoalEntity goal) {
        FinancialGoalDTO.Response dto = new FinancialGoalDTO.Response();
        dto.setId(goal.getId());
        dto.setName(goal.getName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setGoalType(goal.getGoalType());
        dto.setTargetDate(goal.getTargetDate());

        if (goal.getTargetAmount() != null && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            dto.setProgressPercentage(percentage);
        } else {
            dto.setProgressPercentage(BigDecimal.ZERO);
        }
        return dto;
    }
}