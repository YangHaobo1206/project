package com.food.delivery.service;

import com.food.delivery.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User getById(Long id);
    void updateProfile(User user);
}
