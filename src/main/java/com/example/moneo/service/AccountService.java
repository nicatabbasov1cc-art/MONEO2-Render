package com.example.moneo.service;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.entity.AccountEntity;
import com.example.moneo.entity.TransactionEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.AccountRepository;
import com.example.moneo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;


    public List<AccountDTO.AccountResponse> findByUserId(Long userId) {
        List<AccountEntity> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountEntity findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }


    @Transactional
    public AccountDTO.AccountResponse createAccount(AccountDTO.CreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        AccountEntity account = AccountEntity.builder()
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .user(user)
                .build();

        AccountEntity savedAccount = accountRepository.save(account);
        TransactionEntity initialTx = createInitialTransaction(savedAccount, user);

        return convertToDTO(savedAccount, initialTx);
    }

    @Transactional
    public TransactionEntity createInitialTransaction(AccountEntity account, UserEntity user) {
        TransactionEntity transaction = TransactionEntity.builder()
                .amount(account.getBalance())
                .category("Initial Balance")
                .transactionType("INCOME")
                .description("Initial deposit for account opening")
                .transactionDate(LocalDate.now())
                .user(user)
                .account(account)
                .build();

        return transactionRepository.save(transaction);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }


    private AccountDTO.AccountResponse convertToDTO(AccountEntity account) {
        return AccountDTO.AccountResponse.builder()
                .accountId(account.getId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .transactions(List.of()) // Boş siyahı
                .success(true)
                .build();
    }


    private AccountDTO.AccountResponse convertToDTO(AccountEntity account, TransactionEntity transaction) {
        AccountDTO.TransactionResponse txResponse = AccountDTO.TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .categoryName(transaction.getCategory())
                .build();

        return AccountDTO.AccountResponse.builder()
                .accountId(account.getId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .transactions(List.of(txResponse))
                .success(true)
                .build();
    }
}