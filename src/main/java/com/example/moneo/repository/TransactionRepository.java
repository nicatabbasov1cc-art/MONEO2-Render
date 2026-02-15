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

    @Query(value = "SELECT * FROM filter_transactions_func(:userId, :type, :from, :to, CAST(:categoryIds AS bigint[]), :search)",
            countQuery = "SELECT count(*) FROM filter_transactions_func(:userId, :type, :from, :to, CAST(:categoryIds AS bigint[]), :search)",
            nativeQuery = true)
    Page<TransactionEntity> filterTransactions(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("search") String search,
            Pageable pageable);

    List<TransactionEntity> findByUserIdAndDeletedFalseOrderByTransactionDateDesc(Long userId);
    long countByCategoryEntityIdAndDeletedFalse(Long categoryId);


    boolean existsByUserIdAndDeletedFalse(Long userId);
}