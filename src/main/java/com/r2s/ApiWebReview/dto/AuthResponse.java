package com.r2s.ApiWebReview.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresAt;

    public AuthResponse(String accessToken, long expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }
}

