package com.example.moneo.repository;

import com.example.moneo.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {


    List<AccountEntity> findByUserId(Long userId);


    boolean existsByCurrencyAndUserId(String currency, Long userId);


    Optional<AccountEntity> findByIdAndUserId(Long id, Long userId);
}