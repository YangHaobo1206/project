package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.service.OrderService;
import com.example.fooddelivery.service.ShopService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;
    private final OrderService orderService;

    public ShopController(ShopService shopService, OrderService orderService) {
        this.shopService = shopService;
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> create(@RequestBody Shop shop) {
        return ApiResponse.success(shopService.create(shop));
    }

    @GetMapping
    public ApiResponse<List<Shop>> list() {
        return ApiResponse.success(shopService.findAll());
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> goOnline(@PathVariable Long id) {
        return ApiResponse.success(shopService.toggleOnline(id, true));
    }

    @PostMapping("/{id}/offline")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Shop> goOffline(@PathVariable Long id) {
        return ApiResponse.success(shopService.toggleOnline(id, false));
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> orders(@PathVariable Long id) {
        return ApiResponse.success(orderService.findByShop(id));
    }
}
