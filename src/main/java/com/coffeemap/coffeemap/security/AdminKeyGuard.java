package com.coffeemap.coffeemap.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminKeyGuard {

    private final String adminKey;

    public AdminKeyGuard(@Value("${coffeemap.admin.key}") String adminKey) {
        this.adminKey = adminKey;
    }

    public void requireValid(String provided) {
        if (adminKey == null || adminKey.isBlank()) {
            throw new IllegalStateException("Admin key is not configured");
        }
        if (provided == null || !adminKey.equals(provided)) {
            throw new UnauthorizedException("Invalid admin key");
        }
    }
}