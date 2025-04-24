package com.r2s.ApiWebReview.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.r2s.ApiWebReview.common.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleEnum name;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Set<User> users;

}