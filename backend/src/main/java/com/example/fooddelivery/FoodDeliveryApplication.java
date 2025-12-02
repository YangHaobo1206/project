package com.example.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import com.example.fooddelivery.repository.UserRepository;
import com.example.fooddelivery.entity.User;

@SpringBootApplication
public class FoodDeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepository.findByUsername("admin").orElseGet(User::new);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            admin.setPhone("13800000000");
            admin.setAddress("默认管理员地址");
            userRepository.save(admin);

            User demo = userRepository.findByUsername("demo").orElseGet(User::new);
            demo.setUsername("demo");
            demo.setPassword(passwordEncoder.encode("demo"));
            demo.setRole("USER");
            demo.setPhone("13800000001");
            demo.setAddress("默认用户地址");
            userRepository.save(demo);
        };
    }
}
