package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.repository.UserRepository;
import com.example.fooddelivery.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ShopRepository shopRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public Order placeOrder(Order order, Long userId, Long shopId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(404, "User not found"));
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        if (!"APPROVED".equals(shop.getStatus())) {
            throw new BusinessException(400, "店铺未通过审核，暂不可下单");
        }
        if (!shop.isOnline()) {
            throw new BusinessException(400, "店铺未营业");
        }
        order.setUser(user);
        order.setShop(shop);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(java.math.BigDecimal.ZERO);
        }
        return orderRepository.save(order);
    }

    @Override
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(404, "Order not found"));
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    @Override
    public Order markPaid(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(404, "Order not found"));
        order.setStatus("PAID");
        return orderRepository.save(order);
    }

    @Override
    public Order accept(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(404, "Order not found"));
        order.setStatus("ACCEPTED");
        return orderRepository.save(order);
    }

    @Override
    public Order complete(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(404, "Order not found"));
        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findByShop(Long shopId) {
        return orderRepository.findByShopId(shopId);
    }

    @Override
    public List<Order> findByShopOwner(Long ownerId) {
        var shops = shopRepository.findByOwnerId(ownerId);
        if (shops == null || shops.isEmpty()) {
            return List.of();
        }
        var shopIds = shops.stream().map(Shop::getId).toList();
        return orderRepository.findByShopIdIn(shopIds);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public void delete(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new BusinessException(404, "Order not found");
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(404, "Order not found"));
    }
}
