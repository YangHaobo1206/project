package com.example.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.entity.User;

public interface UserService extends IService<User> {
    String login(LoginRequest request);

    User register(RegisterRequest request);

    User currentUser();

    User updateProfile(User user);
}
