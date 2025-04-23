package com.r2s.ApiWebReview.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullname;
    private String role;
}
