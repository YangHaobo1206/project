package com.food.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.food.delivery.common.constant.OrderStatus;
import com.food.delivery.entity.Order;
import com.food.delivery.entity.OrderItem;
import com.food.delivery.mapper.OrderItemMapper;
import com.food.delivery.mapper.OrderMapper;
import com.food.delivery.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        return order;
    }

    @Override
    public void updateStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return;
        }
        order.setStatus(status);
        if (OrderStatus.COMPLETED.name().equals(status)) {
            order.setFinishTime(LocalDateTime.now());
        }
        orderMapper.updateById(order);
    }

    @Override
    public List<Order> listByUser(Long userId) {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId));
    }

    @Override
    public List<Order> listByMerchant(Long merchantId) {
        return orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getMerchantId, merchantId));
    }
}
