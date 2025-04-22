package com.r2s.ApiWebReview.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private long expiresAt;

    public AuthResponse(String token, long expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}

