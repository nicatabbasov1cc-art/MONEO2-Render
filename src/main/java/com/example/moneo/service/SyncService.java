package com.example.moneo.service;

import com.example.moneo.dto.AccountDTO;
import com.example.moneo.dto.CategoryDTO;
import com.example.moneo.dto.SyncDTO;
import com.example.moneo.entity.*;
import com.example.moneo.exception.ResourceNotFoundException;
import com.example.moneo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // Categories sync
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

        // Accounts sync
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

        // Transactions sync
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


        if (request.getOnboardingSelectedCategories() != null) {
            user.setSelectedCategories(request.getOnboardingSelectedCategories());
        }
        if (request.getOnboardingPrimaryGoal() != null) {
            user.setPrimaryGoal(request.getOnboardingPrimaryGoal());
        }
        if (request.getOnboardingWantsAiSuggestions() != null) {
            user.setWantsAiSuggestions(request.getOnboardingWantsAiSuggestions());
        }
        userService.save(user);

        return SyncDTO.SyncResponse.builder()
                .status(conflicts > 0 ? "SYNCED_WITH_CONFLICTS" : "SUCCESS")
                .syncedCount(synced)
                .conflictCount(conflicts)
                .messages(messages)
                .build();
    }


    @Transactional(readOnly = true)
    public SyncDTO.SyncDataResponse getUserData(String email) {
        UserEntity user = userService.findByEmail(email);
        Long userId = user.getId();

        // 1. Accounts
        List<AccountDTO.AccountResponse> accounts = accountRepository.findByUserId(userId).stream()
                .map(account -> AccountDTO.AccountResponse.builder()
                        .accountId(account.getId())
                        .balance(account.getBalance())
                        .currency(account.getCurrency())
                        .transactions(List.of()) // transactions ayrıca gələcək
                        .success(true)
                        .build())
                .collect(Collectors.toList());

        // 2. Categories
        List<CategoryDTO.Response> categories = categoryRepository.findByUserIdOrIsDefaultTrue(userId).stream()
                .map(category -> CategoryDTO.Response.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .icon(category.getIcon())
                        .type(category.getType())
                        .isDefault(category.isDefault())
                        .transactionCount((int) transactionRepository.countByCategoryEntityIdAndDeletedFalse(category.getId()))
                        .build())
                .collect(Collectors.toList());

        // 3. Transactions
        List<AccountDTO.TransactionResponse> transactions = transactionRepository
                .findByUserIdAndDeletedFalseOrderByTransactionDateDesc(userId).stream()
                .limit(50)
                .map(tx -> AccountDTO.TransactionResponse.builder()
                        .id(tx.getId())
                        .type(tx.getTransactionType())
                        .amount(tx.getAmount())
                        .description(tx.getDescription())
                        .categoryName(tx.getCategory())
                        .build())
                .collect(Collectors.toList());

        // 4. Onboarding
        List<String> selectedCategories = user.getSelectedCategories();

        return SyncDTO.SyncDataResponse.builder()
                .accounts(accounts)
                .categories(categories)
                .transactions(transactions)
                .onboardingSelectedCategories(selectedCategories)
                .onboardingPrimaryGoal(user.getPrimaryGoal())
                .onboardingWantsAiSuggestions(user.getWantsAiSuggestions())
                .build();
    }
}