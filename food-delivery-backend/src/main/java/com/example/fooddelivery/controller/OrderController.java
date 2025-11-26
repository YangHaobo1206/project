package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.dto.CreateOrderRequest;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.MerchantService;
import com.example.fooddelivery.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private MerchantService merchantService;

    @PostMapping("/create")
    public Result<Order> create(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/list/user")
    public Result<List<Order>> listByUser() {
        AuthContext.UserSession session = AuthContext.get();
        return Result.success(orderService.listByUser(session.userId()));
    }

    @GetMapping("/list/merchant/{merchantId}")
    public Result<List<Order>> listByMerchant(@PathVariable Long merchantId) {
        return Result.success(orderService.listByMerchant(merchantId));
    }
}
