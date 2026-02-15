package com.example.moneo.controller;

import com.example.moneo.dto.CategoryDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.service.CategoryService;
import com.example.moneo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CategoryDTO.Response> createCategory(@RequestBody CategoryDTO.CreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(categoryService.createCategory(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO.Response>> getCategories() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        return ResponseEntity.ok(categoryService.getAllCategories(user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO.Response> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO.CreateRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);

        return ResponseEntity.ok(categoryService.updateCategory(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByEmail(email);
        categoryService.deleteCategory(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}