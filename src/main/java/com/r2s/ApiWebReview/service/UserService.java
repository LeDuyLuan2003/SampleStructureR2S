package com.r2s.ApiWebReview.service;

import com.r2s.ApiWebReview.entity.User;

public interface UserService {
    User register(User user);
    User findByEmail(String email);
}