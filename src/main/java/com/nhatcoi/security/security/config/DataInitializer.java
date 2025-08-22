package com.nhatcoi.security.security.config;

import com.nhatcoi.security.auth.entity.Role;
import com.nhatcoi.security.auth.entity.User;
import com.nhatcoi.security.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // admin user
        if (!userRepository.existsByEmail("admin@example.com")) {
            User adminUser = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(adminUser);
            log.info("Đã tạo admin user: admin@example.com");
        }

        // moderator user
        if (!userRepository.existsByEmail("moderator@example.com")) {
            User moderatorUser = User.builder()
                    .firstName("Moderator")
                    .lastName("User")
                    .email("moderator@example.com")
                    .password(passwordEncoder.encode("moderator123"))
                    .role(Role.MODERATOR)
                    .enabled(true)
                    .build();
            userRepository.save(moderatorUser);
            log.info("Đã tạo moderator user: moderator@example.com");
        }

        // regular user
        if (!userRepository.existsByEmail("user@example.com")) {
            User regularUser = User.builder()
                    .firstName("Regular")
                    .lastName("User")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .enabled(true)
                    .build();
            userRepository.save(regularUser);
            log.info("Đã tạo regular user: user@example.com");
        }

        log.info("Khởi tạo dữ liệu hoàn tất!");
    }
}
