package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.dto.UpdateUserRequest;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<User> currentUser(Authentication authentication) {
        return userService.findByUsername(authentication.getName())
                .map(ApiResponse::success)
                .orElseGet(() -> ApiResponse.failure(404, "User not found"));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }
}
