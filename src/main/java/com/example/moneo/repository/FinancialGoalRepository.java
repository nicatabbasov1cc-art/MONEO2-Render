package com.example.moneo.repository;

import com.example.moneo.entity.FinancialGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoalEntity, Long> {

    List<FinancialGoalEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}