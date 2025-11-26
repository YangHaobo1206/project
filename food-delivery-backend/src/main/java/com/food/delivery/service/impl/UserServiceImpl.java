package com.food.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.food.delivery.common.constant.RoleType;
import com.food.delivery.common.exception.BizException;
import com.food.delivery.entity.User;
import com.food.delivery.mapper.UserMapper;
import com.food.delivery.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User register(User user) {
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BizException("用户名已存在");
        }
        if (user.getRole() == null) {
            user.setRole(RoleType.USER);
        }
        // 简化：密码未做加密
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (user == null || !Objects.equals(user.getPassword(), password)) {
            throw new BizException("账号或密码错误");
        }
        return user;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateProfile(User user) {
        userMapper.updateById(user);
    }
}
