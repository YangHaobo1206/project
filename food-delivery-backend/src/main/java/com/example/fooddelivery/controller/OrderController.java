package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.dto.CreateOrderRequest;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.entity.Merchant;
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
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");

        Order order = orderService.getById(id);
        if (order == null) throw new BizException(404, "order not found");

        if (!isAdmin(session)) {
            validateMerchantOwner(session, order.getMerchantId());
        }

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
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");

        if (!isAdmin(session)) {
            validateMerchantOwner(session, merchantId);
        }

        return Result.success(orderService.listByMerchant(merchantId));
    }

    private void validateMerchantOwner(AuthContext.UserSession session, Long merchantId) {
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null) throw new BizException(404, "merchant not found");
        if (!merchant.getUserId().equals(session.userId())) {
            throw new BizException(403, "forbidden");
        }
    }

    private boolean isAdmin(AuthContext.UserSession session) {
        return "ADMIN".equals(session.role());
    }
}
