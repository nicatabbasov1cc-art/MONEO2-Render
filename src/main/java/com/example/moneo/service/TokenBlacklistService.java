package com.example.moneo.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String pureToken = token.substring(7);
            blacklistedTokens.add(pureToken);
            System.out.println("LOG: Token qara siyahıya əlavə edildi.");
        } else if (token != null) {
            blacklistedTokens.add(token);
            System.out.println("LOG: Token qara siyahıya əlavə edildi.");
        }
    }

    public boolean isBlacklisted(String token) {
        boolean blacklisted = blacklistedTokens.contains(token);
        if (blacklisted) {
            System.err.println("LOG: Giriş rədd edildi - Token qara siyahıdadır!");
        }
        return blacklisted;
    }
}