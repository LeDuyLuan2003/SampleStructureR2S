package com.r2s.ApiWebReview.config;

import com.r2s.ApiWebReview.common.enums.RoleEnum;
import com.r2s.ApiWebReview.entity.Role;
import com.r2s.ApiWebReview.repository.RoleRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@TestConfiguration
public class TestRoleSeeder {

    @Bean
    @Transactional
    public Runnable seedTestRoles(RoleRepository roleRepository) {
        return () -> {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                if (!roleRepository.existsByName(roleEnum)) {
                    Role role = new Role();
                    role.setName(roleEnum);
                    roleRepository.save(role);
                }
            }
        };
    }
}
