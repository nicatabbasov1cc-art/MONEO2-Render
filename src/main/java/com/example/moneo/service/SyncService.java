package com.example.moneo.service;

import com.example.moneo.dto.SyncDTO;
import com.example.moneo.entity.*;
import com.example.moneo.exception.ResourceNotFoundException;
import com.example.moneo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public SyncDTO.ConflictResponse checkConflict(Long userId, boolean hasLocalData) {
        boolean serverDataExists = transactionRepository.existsByUserIdAndDeletedFalse(userId);
        return SyncDTO.ConflictResponse.builder()
                .serverDataExists(serverDataExists)
                .conflict(hasLocalData && serverDataExists)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public SyncDTO.SyncResponse resolveConflict(SyncDTO.ResolveRequest request, String email) {
        if ("keep_local".equalsIgnoreCase(request.getStrategy())) {
            return syncData(request.getLocalData(), email);
        } else {
            return SyncDTO.SyncResponse.builder()
                    .status("KEEP_SERVER_SUCCESS")
                    .messages(List.of("Server data preserved successfully"))
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public SyncDTO.SyncResponse syncData(SyncDTO.SyncRequest request, String userEmail) {
        UserEntity user = userService.findByEmail(userEmail);
        List<String> messages = new ArrayList<>();
        int synced = 0;
        int conflicts = 0;

        if (request.getCategories() != null) {
            for (var catReq : request.getCategories()) {
                if (!categoryRepository.existsByNameAndUserId(catReq.getName(), user.getId())) {
                    categoryRepository.save(CategoryEntity.builder()
                            .name(catReq.getName()).icon(catReq.getIcon())
                            .type(catReq.getType()).user(user).build());
                    synced++;
                } else {
                    conflicts++;
                    messages.add("Category conflict: " + catReq.getName());
                }
            }
        }

        if (request.getAccounts() != null) {
            for (var accReq : request.getAccounts()) {
                if (!accountRepository.existsByCurrencyAndUserId(accReq.getCurrency(), user.getId())) {
                    accountRepository.save(AccountEntity.builder()
                            .balance(accReq.getBalance())
                            .currency(accReq.getCurrency())
                            .user(user)
                            .build());
                    synced++;
                } else {
                    conflicts++;
                    messages.add("Account conflict (Currency): " + accReq.getCurrency());
                }
            }
        }

        if (request.getTransactions() != null) {
            for (var tReq : request.getTransactions()) {
                AccountEntity account = accountRepository.findByIdAndUserId(tReq.getAccountId(), user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Hesab tapılmadı (ID: " + tReq.getAccountId() + "). Tranzaksiya qeyd edilə bilməz."));

                CategoryEntity category = categoryRepository.findById(tReq.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Kateqoriya tapılmadı (ID: " + tReq.getCategoryId() + ")"));

                transactionRepository.save(TransactionEntity.builder()
                        .amount(tReq.getAmount())
                        .transactionType(tReq.getType().toUpperCase())
                        .description(tReq.getNote())
                        .transactionDate(tReq.getDate())
                        .user(user)
                        .account(account)
                        .category(category.getName())
                        .categoryEntity(category)
                        .build());
                synced++;
            }
        }

        return SyncDTO.SyncResponse.builder()
                .status(conflicts > 0 ? "SYNCED_WITH_CONFLICTS" : "SUCCESS")
                .syncedCount(synced)
                .conflictCount(conflicts)
                .messages(messages)
                .build();
    }
}