package com.food.delivery.service;

import com.food.delivery.entity.Order;
import com.food.delivery.entity.OrderItem;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order, List<OrderItem> items);
    void updateStatus(Long orderId, String status);
    List<Order> listByUser(Long userId);
    List<Order> listByMerchant(Long merchantId);
}
