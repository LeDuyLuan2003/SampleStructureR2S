package com.r2s.ApiWebReview.repository;

import com.r2s.ApiWebReview.common.enums.RoleEnum;
import com.r2s.ApiWebReview.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
    boolean existsByName(RoleEnum name);
    boolean existsById(Long id);
}
