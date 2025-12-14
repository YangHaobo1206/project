package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.service.OrderService;
import com.example.fooddelivery.service.ShopService;
import com.example.fooddelivery.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;
    private final OrderService orderService;
    private final UserService userService;

    public ShopController(ShopService shopService, OrderService orderService, UserService userService) {
        this.shopService = shopService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> create(@RequestBody Shop shop) {
        return ApiResponse.success(shopService.create(shop));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> approve(@PathVariable Long id) {
        return ApiResponse.success(shopService.approve(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> reject(@PathVariable Long id) {
        return ApiResponse.success(shopService.reject(id));
    }

    @GetMapping
    public ApiResponse<List<Shop>> list() {
        return ApiResponse.success(shopService.findAll());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('MERCHANT','ADMIN')")
    public ApiResponse<List<Shop>> mine(Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(shopService.findByOwner(uid));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> update(@PathVariable Long id, @RequestBody Shop shop) {
        return ApiResponse.success(shopService.update(id, shop));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        shopService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/online")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Shop> goOnline(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(shopService.toggleOnline(id, true, uid, isAdmin));
    }

    @PostMapping("/{id}/offline")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Shop> goOffline(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(shopService.toggleOnline(id, false, uid, isAdmin));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> orders(@PathVariable Long id) {
        return ApiResponse.success(orderService.findByShop(id));
    }
}
