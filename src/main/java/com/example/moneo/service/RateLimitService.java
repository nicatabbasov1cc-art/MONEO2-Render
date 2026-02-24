package com.example.moneo.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> loginCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerCache = new ConcurrentHashMap<>();

    private Bucket createLoginBucket() {
        // 5 cəhd / dəqiqə
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createRegisterBucket() {
        // 3 cəhd / 10 dəqiqə
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(10)));
        return Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsumeLogin(String email) {
        boolean allowed = loginCache.computeIfAbsent(email, k -> createLoginBucket()).tryConsume(1);
        System.out.println("Rate Limit (Login) yoxlanışı: " + email + " -> İcazə: " + allowed);
        return allowed;
    }

    public boolean tryConsumeRegister(String email) {
        boolean allowed = registerCache.computeIfAbsent(email, k -> createRegisterBucket()).tryConsume(1);
        System.out.println("Rate Limit (Register) yoxlanışı: " + email + " -> İcazə: " + allowed);
        return allowed;
    }

    // Köhnə metod — geriyə uyğunluq üçün saxlanılır
    public boolean tryConsume(String email) {
        return tryConsumeLogin(email);
    }
}