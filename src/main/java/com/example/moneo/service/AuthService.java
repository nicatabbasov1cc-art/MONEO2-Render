package com.example.moneo.service;

import com.example.moneo.dto.AuthDTO;
import com.example.moneo.entity.UserEntity;
import com.example.moneo.repository.UserRepository;
import com.example.moneo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RateLimitService rateLimitService;

    public String register(AuthDTO.RegisterRequest request) {

        if (!request.getPassword().equals(request.getRepassword())) {
            throw new RuntimeException("PASSWORDS_DO_NOT_MATCH");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }


        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return "Registration successful";
    }

    @Transactional(readOnly = true)
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        System.out.println("LOG: Giriş cəhdi yoxlanılır - Email: " + request.getEmail());

        if (!rateLimitService.tryConsume(request.getEmail())) {
            System.err.println("LOG: !!! RATE LIMIT ASHILDI !!! - " + request.getEmail());
            throw new RuntimeException("TOO_MANY_REQUESTS");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            System.out.println("LOG: Səhv şifrə cəhdi - " + request.getEmail());
            throw new RuntimeException("WRONG_CREDENTIALS");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        String token = jwtUtil.generateToken(user.getEmail());
        boolean hasData = user.getTransactions() != null && !user.getTransactions().isEmpty();


        return AuthDTO.AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .hasData(hasData)
                .success(true)
                .build();
    }
}