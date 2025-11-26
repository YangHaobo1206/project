package com.example.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.dto.CreateOrderRequest;
import com.example.fooddelivery.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    Order createOrder(CreateOrderRequest request);

    void updateStatus(Long id, String status);

    List<Order> listByUser(Long userId);

    List<Order> listByMerchant(Long merchantId);
}
