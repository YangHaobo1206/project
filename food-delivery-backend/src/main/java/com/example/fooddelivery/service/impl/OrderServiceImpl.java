package com.example.fooddelivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.dto.CreateOrderRequest;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.entity.OrderItem;
import com.example.fooddelivery.mapper.DishMapper;
import com.example.fooddelivery.mapper.OrderItemMapper;
import com.example.fooddelivery.mapper.OrderMapper;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Override
    public Order createOrder(CreateOrderRequest request) {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.Item item : request.getItems()) {
            Dish dish = dishMapper.selectById(item.getDishId());
            if (dish == null) {
                throw new BizException(404, "dish not found");
            }
            BigDecimal price = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(price);
            OrderItem orderItem = new OrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(dish.getPrice());
            items.add(orderItem);
        }
        Order order = new Order();
        order.setUserId(session.userId());
        order.setMerchantId(request.getMerchantId());
        order.setAmount(total);
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        save(order);
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }
        return order;
    }

    @Override
    public void updateStatus(Long id, String status) {
        Order order = getById(id);
        if (order == null) throw new BizException(404, "order not found");
        order.setStatus(status);
        updateById(order);
    }

    @Override
    public List<Order> listByUser(Long userId) {
        return lambdaQuery().eq(Order::getUserId, userId).list();
    }

    @Override
    public List<Order> listByMerchant(Long merchantId) {
        return lambdaQuery().eq(Order::getMerchantId, merchantId).list();
    }
}
