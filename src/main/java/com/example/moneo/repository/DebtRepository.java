package com.example.moneo.repository;

import com.example.moneo.entity.DebtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<DebtEntity, Long> {
    List<DebtEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}