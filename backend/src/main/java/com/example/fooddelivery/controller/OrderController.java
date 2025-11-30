package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.service.OrderService;
import com.example.fooddelivery.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<Order> placeOrder(@RequestParam Long shopId, @RequestBody Order order, Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ApiResponse.success(orderService.placeOrder(order, userId, shopId));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Order> cancel(@PathVariable Long id, Authentication authentication) {
        Order target = orderService.findById(id);
        boolean isAdmin = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_ADMIN"));
        if (!isAdmin && target.getUser() != null && !authentication.getName().equals(target.getUser().getUsername())) {
            throw new BusinessException(403, "无权限取消该订单");
        }
        return ApiResponse.success(orderService.cancel(id));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Order> pay(@PathVariable Long id) {
        return ApiResponse.success(orderService.markPaid(id));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Order> accept(@PathVariable Long id) {
        return ApiResponse.success(orderService.accept(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Order> complete(@PathVariable Long id) {
        return ApiResponse.success(orderService.complete(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> listAll() {
        return ApiResponse.success(orderService.findAll());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> byUser(@PathVariable Long userId) {
        return ApiResponse.success(orderService.findByUser(userId));
    }

    @GetMapping("/shop/{shopId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> byShop(@PathVariable Long shopId) {
        return ApiResponse.success(orderService.findByShop(shopId));
    }

    @GetMapping("/my")
    public ApiResponse<List<Order>> myOrders(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ApiResponse.success(orderService.findByUser(userId));
    }
}
