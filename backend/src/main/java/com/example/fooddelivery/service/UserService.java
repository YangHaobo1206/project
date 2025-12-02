package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.dto.UpdateUserRequest;
import com.example.fooddelivery.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(RegisterRequest request);

    String login(LoginRequest request);

    User update(Long id, UpdateUserRequest request);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void delete(Long id);
}
