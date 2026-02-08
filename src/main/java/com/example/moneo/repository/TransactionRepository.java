package com.example.moneo.repository;

import com.example.moneo.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.user.id = :userId AND t.deleted = false " +
            "AND (:type IS NULL OR t.transactionType = :type) " +
            "AND (:from IS NULL OR t.transactionDate >= :from) " +
            "AND (:to IS NULL OR t.transactionDate <= :to)")
    Page<TransactionEntity> filterTransactions(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);

    List<TransactionEntity> findByUserIdAndDeletedFalseOrderByTransactionDateDesc(Long userId);

    long countByCategoryEntityIdAndDeletedFalse(Long categoryId);
}