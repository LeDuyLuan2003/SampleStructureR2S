package com.r2s.ApiWebReview.config;

import com.r2s.ApiWebReview.common.enums.RoleEnum;
import com.r2s.ApiWebReview.entity.Role;
import com.r2s.ApiWebReview.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Seed role USER nếu chưa có
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
                System.out.println("🔰 Seeded role: " + roleEnum.name());
            }
        }
    }
}

