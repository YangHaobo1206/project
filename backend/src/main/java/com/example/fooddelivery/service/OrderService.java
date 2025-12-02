package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Order order, Long userId, Long shopId);
    Order cancel(Long orderId);
    Order markPaid(Long orderId);
    Order accept(Long orderId);
    Order complete(Long orderId);
    List<Order> findByUser(Long userId);
    List<Order> findByShop(Long shopId);
    List<Order> findAll();
    void delete(Long orderId);
    Order findById(Long orderId);
}
