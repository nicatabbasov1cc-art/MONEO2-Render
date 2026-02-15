package com.example.moneo.service;

import com.example.moneo.entity.DebtEntity;
import com.example.moneo.repository.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;

    public DebtEntity save(DebtEntity debt) {
        return debtRepository.save(debt);
    }

    public List<DebtEntity> getUserDebts(Long userId) {
        return debtRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<DebtEntity> findById(Long id) {
        return debtRepository.findById(id);
    }

    public void deleteById(Long id) {
        debtRepository.deleteById(id);
    }
}