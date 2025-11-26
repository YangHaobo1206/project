package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.entity.Merchant;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.MerchantService;
import com.example.fooddelivery.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private MerchantService merchantService;
    @Resource
    private UserService userService;

    private void checkAdmin() {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null || !"ADMIN".equals(session.role())) {
            throw new BizException(403, "forbidden");
        }
    }

    @GetMapping("/merchant/pending")
    public Result<List<Merchant>> pendingMerchants() {
        checkAdmin();
        return Result.success(merchantService.pendingMerchants());
    }

    @PostMapping("/merchant/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean pass) {
        checkAdmin();
        merchantService.approve(id, pass);
        return Result.success();
    }

    @GetMapping("/users")
    public Result<List<User>> users() {
        checkAdmin();
        return Result.success(userService.list());
    }
}
