package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.dto.LoginRequest;
import com.example.fooddelivery.dto.RegisterRequest;
import com.example.fooddelivery.dto.UpdateUserRequest;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.repository.UserRepository;
import com.example.fooddelivery.service.UserService;
import com.example.fooddelivery.util.JwtUtil;
import com.example.fooddelivery.service.ShopService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ShopService shopService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, ShopService shopService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.shopService = shopService;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "User already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        String role = request.getRole() != null ? request.getRole().toUpperCase() : "USER";
        if ("MERCHANT".equals(role)) {
            if (request.getShopName() == null || request.getShopAddress() == null) {
                throw new BusinessException(400, "商家注册需要提供店铺名称与地址");
            }
        }
        user.setRole(role);
        User saved = userRepository.save(user);

        if ("MERCHANT".equals(role)) {
            Shop shop = new Shop();
            shop.setName(request.getShopName());
            shop.setDescription(request.getShopDescription());
            shop.setAddress(request.getShopAddress());
            shopService.createForMerchant(shop, saved.getId());
        }
        return saved;
    }

    @Override
    public String login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        return jwtUtil.generateToken(request.getUsername());
    }

    @Override
    public User update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(404, "User not found"));
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(404, "User not found");
        }
        userRepository.deleteById(id);
    }
}
