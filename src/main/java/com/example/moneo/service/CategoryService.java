package com.example.moneo.service;

import com.example.moneo.dto.CategoryDTO;
import com.example.moneo.entity.CategoryEntity;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.CategoryRepository;
import com.example.moneo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public CategoryDTO.Response createCategory(CategoryDTO.CreateRequest request, UserEntity user) {

        if (categoryRepository.existsByNameAndUserId(request.getName(), user.getId())) {
            throw new RuntimeException("CATEGORY_ALREADY_EXISTS");
        }


        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .type(request.getType())
                .isDefault(false)
                .user(user)
                .build();

        CategoryEntity saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public List<CategoryDTO.Response> getAllCategories(Long userId) {
        return categoryRepository.findByUserIdOrIsDefaultTrue(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("CATEGORY_NOT_FOUND"));

        if (category.isDefault()) {
            throw new RuntimeException("DEFAULT_CATEGORY_CANNOT_BE_DELETED");
        }

        if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
            throw new RuntimeException("ACCESS_DENIED");
        }

        long count = transactionRepository.countByCategoryEntityIdAndDeletedFalse(categoryId);
        if (count > 0) {
            throw new RuntimeException("CATEGORY_HAS_TRANSACTIONS");
        }

        categoryRepository.delete(category);
    }

    private CategoryDTO.Response mapToResponse(CategoryEntity entity) {
        long count = transactionRepository.countByCategoryEntityIdAndDeletedFalse(entity.getId());

        return CategoryDTO.Response.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .type(entity.getType())
                .isDefault(entity.isDefault())
                .transactionCount((int) count)
                .build();
    }
}