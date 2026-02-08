package com.example.moneo.service;

import com.example.moneo.entity.UserEntity;
import com.example.moneo.exception.ResourceNotFoundException;
import com.example.moneo.exception.AlreadyExistsException;
import com.example.moneo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}