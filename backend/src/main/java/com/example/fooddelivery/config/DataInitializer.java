package com.example.fooddelivery.config;

import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setRole("ADMIN");
                admin.setPhone("18800000000");
                userRepository.save(admin);
                log.info("Seeded default admin user: admin / Admin123!");
            }

            if (userRepository.findByUsername("demo").isEmpty()) {
                User demo = new User();
                demo.setUsername("demo");
                demo.setPassword(passwordEncoder.encode("Demo123!"));
                demo.setRole("USER");
                demo.setPhone("17700000000");
                userRepository.save(demo);
                log.info("Seeded demo user: demo / Demo123!");
            }
        };
    }
}
