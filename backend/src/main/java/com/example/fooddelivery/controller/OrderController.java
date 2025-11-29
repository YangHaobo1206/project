package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<Order> placeOrder(@RequestParam Long userId, @RequestParam Long shopId, @RequestBody Order order) {
        return ApiResponse.success(orderService.placeOrder(order, userId, shopId));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Order> cancel(@PathVariable Long id) {
        return ApiResponse.success(orderService.cancel(id));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<Order> pay(@PathVariable Long id) {
        return ApiResponse.success(orderService.markPaid(id));
    }

    @PostMapping("/{id}/accept")
    public ApiResponse<Order> accept(@PathVariable Long id) {
        return ApiResponse.success(orderService.accept(id));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> byUser(@PathVariable Long userId) {
        return ApiResponse.success(orderService.findByUser(userId));
    }

    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<Order>> byShop(@PathVariable Long shopId) {
        return ApiResponse.success(orderService.findByShop(shopId));
    }
}
