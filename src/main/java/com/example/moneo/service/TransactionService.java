package com.example.moneo.service;

import com.example.moneo.dto.DashboardDTO;
import com.example.moneo.dto.TransactionDTO;
import com.example.moneo.entity.TransactionEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionEntity save(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }


    @Transactional
    public TransactionEntity createTransaction(TransactionDTO.CreateRequest request, UserEntity user) {
        TransactionEntity transaction = TransactionEntity.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .type(request.getType())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .user(user)
                .build();

        return save(transaction);
    }

    public List<TransactionEntity> getUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public DashboardDTO getDashboardSummary(Long userId) {
        List<TransactionEntity> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        BigDecimal income = transactions.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getType()))
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getType()))
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String suggestion = "Maliyyə vəziyyətiniz stabildir.";
        if (expense.compareTo(income) > 0 && income.compareTo(BigDecimal.ZERO) > 0) {
            suggestion = "Diqqət: Bu ay xərcləriniz gəlirinizi üstələyib!";
        }

        return DashboardDTO.builder()
                .totalBalance(income.subtract(expense))
                .monthlyIncome(income)
                .monthlyExpense(expense)
                .aiSuggestion(suggestion)
                .lastTransactions(transactions.stream()
                        .limit(5)
                        .map(this::convertToDTO)
                        .toList())
                .build();
    }

    private TransactionDTO.Response convertToDTO(TransactionEntity entity) {
        TransactionDTO.Response dto = new TransactionDTO.Response();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setCategory(entity.getCategory());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setTransactionDate(entity.getTransactionDate());
        return dto;
    }
}