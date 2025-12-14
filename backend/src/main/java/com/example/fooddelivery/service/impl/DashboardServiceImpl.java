package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.dto.ChartItem;
import com.example.fooddelivery.dto.DashboardResponse;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Order;
import com.example.fooddelivery.repository.DishRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.repository.UserRepository;
import com.example.fooddelivery.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                DishRepository dishRepository,
                                OrderRepository orderRepository,
                                ShopRepository shopRepository) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public DashboardResponse getAdminDashboard() {
        long userCount = userRepository.count();
        List<ChartItem> dishStats = groupDishByCategory(dishRepository.findAll());
        List<Order> orders = orderRepository.findAll();
        List<ChartItem> orderStats = groupOrderByStatus(orders);
        List<ChartItem> orderByDate = groupOrderByDate(orders);
        return new DashboardResponse(userCount, dishStats, orderStats, orderByDate);
    }

    @Override
    public DashboardResponse getMerchantDashboard(Long ownerId) {
        var shops = shopRepository.findByOwnerId(ownerId);
        if (shops == null || shops.isEmpty()) {
            return new DashboardResponse(0L, List.of(), List.of(), List.of());
        }
        var shopIds = shops.stream().map(s -> s.getId()).toList();
        List<Dish> dishes = dishRepository.findByShopIdIn(shopIds);
        List<Order> orders = orderRepository.findByShopIdIn(shopIds);
        List<ChartItem> dishStats = groupDishByCategory(dishes);
        List<ChartItem> orderStats = groupOrderByStatus(orders);
        List<ChartItem> orderByDate = groupOrderByDate(orders);
        return new DashboardResponse(0L, dishStats, orderStats, orderByDate);
    }

    private List<ChartItem> groupDishByCategory(List<Dish> dishes) {
        Map<String, Long> map = dishes.stream()
                .collect(Collectors.groupingBy(d -> d.getCategory() == null || d.getCategory().isBlank() ? "未分类" : d.getCategory(), Collectors.counting()));
        return map.entrySet().stream().map(e -> new ChartItem(e.getKey(), e.getValue())).toList();
    }

    private List<ChartItem> groupOrderByStatus(List<Order> orders) {
        Map<String, Long> map = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus() == null ? "UNKNOWN" : o.getStatus(), Collectors.counting()));
        return map.entrySet().stream().map(e -> new ChartItem(e.getKey(), e.getValue())).toList();
    }

    private List<ChartItem> groupOrderByDate(List<Order> orders) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        Map<LocalDate, Long> map = new HashMap<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            map.put(d, 0L);
        }
        for (Order o : orders) {
            if (o.getCreatedAt() == null) continue;
            LocalDate d = o.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!d.isBefore(start) && !d.isAfter(today)) {
                map.put(d, map.getOrDefault(d, 0L) + 1);
            }
        }
        List<ChartItem> list = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            list.add(new ChartItem(d.toString(), map.getOrDefault(d, 0L)));
        }
        return list;
    }
}
