package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        return Result.success(userService.currentUser());
    }

    @PutMapping("/profile")
    public Result<User> update(@RequestBody User user) {
        return Result.success(userService.updateProfile(user));
    }
}
