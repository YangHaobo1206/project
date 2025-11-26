package com.food.delivery.controller;

import com.food.delivery.common.jwt.JwtUtil;
import com.food.delivery.common.jwt.LoginUser;
import com.food.delivery.common.response.Result;
import com.food.delivery.entity.User;
import com.food.delivery.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User saved = userService.register(user);
        return Result.success(saved);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        String token = JwtUtil.createToken(user.getId(), user.getRole());
        return Result.success(token);
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        Long uid = LoginUser.get().getId();
        return Result.success(userService.getById(uid));
    }

    @PutMapping("/profile")
    public Result<?> update(@RequestBody User user) {
        user.setId(LoginUser.get().getId());
        userService.updateProfile(user);
        return Result.success();
    }

    @Data
    public static class LoginDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
