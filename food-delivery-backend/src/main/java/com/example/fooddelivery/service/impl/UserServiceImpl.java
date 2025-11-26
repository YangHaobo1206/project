package com.example.fooddelivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.mapper.UserMapper;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.security.JwtUtil;
import com.example.fooddelivery.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtUtil jwtUtil;

    private String encode(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String login(LoginRequest request) {
        User user = lambdaQuery().eq(User::getUsername, request.getUsername()).one();
        if (user == null || !user.getPassword().equals(encode(request.getPassword()))) {
            throw new BizException(401, "username or password wrong");
        }
        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    @Override
    public User register(RegisterRequest request) {
        if (lambdaQuery().eq(User::getUsername, request.getUsername()).one() != null) {
            throw new BizException(400, "username exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encode(request.getPassword()));
        user.setRole("USER");
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCreateTime(LocalDateTime.now());
        save(user);
        return user;
    }

    @Override
    public User currentUser() {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) {
            throw new BizException(401, "Unauthorized");
        }
        return getById(session.userId());
    }

    @Override
    public User updateProfile(User update) {
        User current = currentUser();
        current.setNickname(update.getNickname());
        current.setPhone(update.getPhone());
        current.setAddress(update.getAddress());
        updateById(current);
        return current;
    }
}
