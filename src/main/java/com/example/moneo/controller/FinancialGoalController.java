package com.example.moneo.controller;

import com.example.moneo.dto.FinancialGoalDTO;
import com.example.moneo.entity.FinancialGoalEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.FinancialGoalService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class FinancialGoalController {

    private final FinancialGoalService goalService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<FinancialGoalDTO.Response> createGoal(@RequestBody FinancialGoalDTO.CreateRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(currentUserEmail);

        FinancialGoalEntity goal = FinancialGoalEntity.builder()
                .name(request.getName())
                .targetAmount(request.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .goalType(request.getGoalType())
                .targetDate(request.getTargetDate())
                .durationMonths(request.getDurationMonths())
                .icon(request.getIcon())
                .user(user)
                .build();

        return ResponseEntity.ok(convertToDTO(goalService.save(goal)));
    }

    @GetMapping
    public ResponseEntity<List<FinancialGoalDTO.Response>> getUserGoals() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        return ResponseEntity.ok(goalService.getUserGoals(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable Long id, @RequestBody FinancialGoalDTO.CreateRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<FinancialGoalEntity> goalOpt = goalService.findById(id);

        if (goalOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Məqsəd tapılmadı.");
        }

        FinancialGoalEntity existingGoal = goalOpt.get();

        if (!existingGoal.getUser().getEmail().equals(currentUserEmail)) {
            return ResponseEntity.status(403).body("Bu məqsədi redaktə etmək icazəniz yoxdur!");
        }

        existingGoal.setName(request.getName());
        existingGoal.setTargetAmount(request.getTargetAmount());
        existingGoal.setCurrentAmount(request.getCurrentAmount());
        existingGoal.setDurationMonths(request.getDurationMonths());
        existingGoal.setIcon(request.getIcon());
        existingGoal.setGoalType(request.getGoalType());
        existingGoal.setTargetDate(request.getTargetDate());

        return ResponseEntity.ok(convertToDTO(goalService.save(existingGoal)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<FinancialGoalEntity> goalOpt = goalService.findById(id);

        if (goalOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Məqsəd tapılmadı.");
        }

        FinancialGoalEntity goal = goalOpt.get();


        if (!goal.getUser().getEmail().equals(currentUserEmail)) {
            return ResponseEntity.status(403).body("Bu məqsədi silmək icazəniz yoxdur!");
        }

        goalService.deleteById(id);
        return ResponseEntity.ok().body("Məqsəd uğurla silindi. ID: " + id);
    }

    private FinancialGoalDTO.Response convertToDTO(FinancialGoalEntity goal) {
        BigDecimal percentage = BigDecimal.ZERO;
        if (goal.getTargetAmount() != null && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentage = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return FinancialGoalDTO.Response.builder()
                .id(goal.getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .progressPercentage(percentage)
                .goalType(goal.getGoalType())
                .targetDate(goal.getTargetDate())
                .durationMonths(goal.getDurationMonths())
                .icon(goal.getIcon())
                .createdAt(goal.getCreatedAt())
                .build();
    }
}