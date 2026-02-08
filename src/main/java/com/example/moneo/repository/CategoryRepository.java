package com.example.moneo.repository;

import com.example.moneo.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findByUserIdOrIsDefaultTrue(Long userId);

    boolean existsByNameAndUserId(String name, Long userId);

    Optional<CategoryEntity> findByNameAndUserId(String name, Long userId);
}