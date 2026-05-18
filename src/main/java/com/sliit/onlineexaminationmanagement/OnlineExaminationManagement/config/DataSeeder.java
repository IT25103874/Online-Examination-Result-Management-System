package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.config;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Admin;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.User;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.AdminRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public void run(String... args) {

        // only create if not already exists
        if (!userRepository.existsByEmail("admin@exam.com")) {

            // 1. create User
            User user = new User();
            user.setName("Admin");
            user.setEmail("admin@exam.com");
            user.setPassword("admin123");  // plain text for now, add BCrypt later
            user.setRole("ADMIN");
            user.setStatus("ACTIVE");
            userRepository.save(user);

            // 2. create Admin profile
            Admin admin = new Admin();
            admin.setName("Admin");
            admin.setEmail("admin@exam.com");
            admin.setPassword("admin123");
            admin.setPhone("0000000000");
            admin.setUser(user);
            adminRepository.save(admin);

            System.out.println("✅ Admin seeded: admin@exam.com / admin123");
        } else {
            System.out.println("✅ Admin already exists, skipping seed.");
        }
    }
}
