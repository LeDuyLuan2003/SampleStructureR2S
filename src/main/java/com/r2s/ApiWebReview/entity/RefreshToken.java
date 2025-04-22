package com.r2s.ApiWebReview.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Gắn 1 user với refresh token
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Mã token (UUID random)
    @Column(nullable = false, unique = true)
    private String token;

    // Thời gian hết hạn
    @Column(nullable = false)
    private Instant expiryDate;
}
