package com.example.moneo.service;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.dto.TransactionDTO;
import com.example.moneo.entity.AccountEntity;
import com.example.moneo.entity.CategoryEntity;
import com.example.moneo.entity.TransactionEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.AccountRepository;
import com.example.moneo.repository.CategoryRepository;
import com.example.moneo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionDTO.Response createTransaction(TransactionDTO.CreateRequest request, UserEntity user) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("ACCOUNT_NOT_FOUND"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        TransactionEntity transaction = TransactionEntity.builder()
                .amount(request.getAmount())
                .category(category.getName())
                .categoryEntity(category)
                .transactionType(request.getType().toUpperCase())
                .description(request.getNote())
                .transactionDate(request.getDate())
                .user(user)
                .account(account)
                .build();

        updateAccountBalance(account, transaction.getAmount(), transaction.getTransactionType(), false);

        accountRepository.save(account);
        TransactionEntity saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionDTO.Response updateTransaction(Long id, TransactionDTO.CreateRequest request, Long userId) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("TRANSACTION_NOT_FOUND_OR_ACCESS_DENIED"));


        updateAccountBalance(transaction.getAccount(), transaction.getAmount(), transaction.getTransactionType(), true);
        accountRepository.save(transaction.getAccount());


        AccountEntity newAccount = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("ACCOUNT_NOT_FOUND"));
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));


        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(request.getType().toUpperCase());
        transaction.setTransactionDate(request.getDate());
        transaction.setCategoryEntity(category);
        transaction.setCategory(category.getName());
        transaction.setDescription(request.getNote());
        transaction.setAccount(newAccount);


        updateAccountBalance(newAccount, transaction.getAmount(), transaction.getTransactionType(), false);
        accountRepository.save(newAccount);

        TransactionEntity updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTransaction(Long id, Long userId) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("TRANSACTION_NOT_FOUND"));


        updateAccountBalance(transaction.getAccount(), transaction.getAmount(), transaction.getTransactionType(), true);
        accountRepository.save(transaction.getAccount());

        transaction.setDeleted(true); // Soft delete
        transactionRepository.save(transaction);
    }

    private void updateAccountBalance(AccountEntity account, BigDecimal amount, String type, boolean isRevert) {
        boolean isIncome = "INCOME".equalsIgnoreCase(type);
        if (isRevert) {
            if (isIncome) account.setBalance(account.getBalance().subtract(amount));
            else account.setBalance(account.getBalance().add(amount));
        } else {
            if (isIncome) account.setBalance(account.getBalance().add(amount));
            else account.setBalance(account.getBalance().subtract(amount));
        }
    }

    private TransactionDTO.Response mapToResponse(TransactionEntity saved) {
        return TransactionDTO.Response.builder()
                .id(saved.getId())
                .amount(saved.getAmount())
                .type(saved.getTransactionType())
                .note(saved.getDescription())
                .date(saved.getTransactionDate())
                .categoryId(saved.getCategoryEntity() != null ? saved.getCategoryEntity().getId() : null)
                .categoryName(saved.getCategory())
                .build();
    }

    // Dashboard və List metodları olduğu kimi qalır...
    public AccountDTO.DashboardResponse getDashboardSummary(Long userId) {
        List<TransactionEntity> transactions = transactionRepository.findByUserIdAndDeletedFalseOrderByTransactionDateDesc(userId);
        BigDecimal income = transactions.stream()
                .filter(t -> "INCOME".equalsIgnoreCase(t.getTransactionType()))
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = transactions.stream()
                .filter(t -> "EXPENSE".equalsIgnoreCase(t.getTransactionType()))
                .map(TransactionEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AccountDTO.TransactionResponse> recent = transactions.stream()
                .limit(5)
                .map(t -> AccountDTO.TransactionResponse.builder()
                        .id(t.getId())
                        .type(t.getTransactionType())
                        .amount(t.getAmount())
                        .description(t.getDescription())
                        .categoryName(t.getCategory())
                        .build())
                .collect(Collectors.toList());

        return AccountDTO.DashboardResponse.builder()
                .balance(income.subtract(expenses))
                .thisMonthIncome(income)
                .thisMonthExpenses(expenses)
                .recentTransactions(recent)
                .trend("Stabil")
                .performanceIndex(100.0)
                .build();
    }

    public AccountDTO.TransactionListResponse getTransactions(
            Long userId, String type, LocalDate from, LocalDate to, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("transactionDate").descending());
        Page<TransactionEntity> result = transactionRepository.filterTransactions(userId, type, from, to, pageable);
        List<AccountDTO.TransactionResponse> txList = result.getContent().stream()
                .map(t -> AccountDTO.TransactionResponse.builder()
                        .id(t.getId())
                        .type(t.getTransactionType())
                        .amount(t.getAmount())
                        .description(t.getDescription())
                        .categoryName(t.getCategory())
                        .build())
                .collect(Collectors.toList());
        return AccountDTO.TransactionListResponse.builder()
                .transactions(txList)
                .pagination(AccountDTO.PaginationResponse.builder()
                        .currentPage(result.getNumber())
                        .totalPages(result.getTotalPages())
                        .totalCount(result.getTotalElements())
                        .build())
                .build();
    }
}