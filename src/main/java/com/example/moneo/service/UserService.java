package com.example.moneo.service;

import com.example.moneo.dto.UserDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.exception.ResourceNotFoundException;
import com.example.moneo.exception.AlreadyExistsException;
import com.example.moneo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + email));
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public void register(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException("Bu email ünvanı artıq istifadə olunub.");
        }
        this.save(user);
    }

    public UserDTO.UserResponse getUserProfile(String email) {
        UserEntity user = findByEmail(email);

        return UserDTO.UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .preferences(UserDTO.PreferencesDTO.builder()
                        .smartSuggestionsEnabled(Boolean.TRUE.equals(user.getWantsAiSuggestions()))
                        .build())
                .build();
    }

    @Transactional
    public UserDTO.PreferencesDTO updatePreferences(String email, UserDTO.UpdatePreferencesRequest request) {
        UserEntity user = findByEmail(email);

        user.setWantsAiSuggestions(request.isSmartSuggestionsEnabled());
        userRepository.save(user);

        return UserDTO.PreferencesDTO.builder()
                .smartSuggestionsEnabled(user.getWantsAiSuggestions())
                .build();
    }


    @Transactional
    public void clearUserData(String email) {
        UserEntity user = findByEmail(email);


        if (user.getTransactions() != null) {
            user.getTransactions().clear();
        }

        if (user.getAccounts() != null) {
            user.getAccounts().clear();
        }

        if (user.getSelectedCategories() != null) {
            user.getSelectedCategories().clear();
        }

        userRepository.save(user);
    }
}