package com.food.delivery.controller;

import com.food.delivery.common.jwt.LoginUser;
import com.food.delivery.common.response.Result;
import com.food.delivery.entity.Order;
import com.food.delivery.entity.OrderItem;
import com.food.delivery.service.OrderService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping("/create")
    public Result<Order> create(@RequestBody CreateOrderDTO dto) {
        Order order = new Order();
        order.setUserId(LoginUser.get().getId());
        order.setMerchantId(dto.getMerchantId());
        order.setAddress(dto.getAddress());
        order.setTotalAmount(dto.getTotalAmount());
        Order saved = orderService.createOrder(order, dto.getItems());
        return Result.success(saved);
    }

    @GetMapping("/list/user")
    public Result<List<Order>> listForUser() {
        Long uid = LoginUser.get().getId();
        return Result.success(orderService.listByUser(uid));
    }

    @GetMapping("/list/merchant/{merchantId}")
    public Result<List<Order>> listForMerchant(@PathVariable Long merchantId) {
        return Result.success(orderService.listByMerchant(merchantId));
    }

    @PutMapping("/{orderId}/status")
    public Result<?> updateStatus(@PathVariable Long orderId,
                                  @RequestParam String status) {
        orderService.updateStatus(orderId, status);
        return Result.success();
    }

    @Data
    public static class CreateOrderDTO {
        private Long merchantId;
        private String address;
        private BigDecimal totalAmount;
        private List<OrderItem> items;
    }
}
