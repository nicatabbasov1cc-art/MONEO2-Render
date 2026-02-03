package com.example.moneo.service;

import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public void register(UserEntity user) {
        this.save(user);
    }
}