package com.example.moneo.service;

import com.example.moneo.entity.FinancialGoalEntity;
import com.example.moneo.repository.FinancialGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialGoalService {
    private final FinancialGoalRepository goalRepository;

    public FinancialGoalEntity save(FinancialGoalEntity goal) {
        return goalRepository.save(goal);
    }

    public List<FinancialGoalEntity> getUserGoals(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<FinancialGoalEntity> findById(Long id) {
        return goalRepository.findById(id);
    }
}