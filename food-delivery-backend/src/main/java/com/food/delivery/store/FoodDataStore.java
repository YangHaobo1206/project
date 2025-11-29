package com.food.delivery.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FoodDataStore {
    private final List<Merchant> merchants = new ArrayList<>();
    private final List<Dish> dishes = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final AtomicLong orderId = new AtomicLong(1);

    public FoodDataStore() {
        merchants.add(new Merchant(1, "Sushi World", "Tokyo St"));
        merchants.add(new Merchant(2, "Pasta House", "Rome Ave"));

        dishes.add(new Dish(1, 1, "Salmon Roll", 12.50));
        dishes.add(new Dish(2, 1, "Tuna Nigiri", 9.80));
        dishes.add(new Dish(3, 2, "Carbonara", 14.30));
    }

    public List<Merchant> getMerchants() {
        return Collections.unmodifiableList(merchants);
    }

    public List<Dish> getDishes(Optional<Long> merchantId) {
        if (merchantId.isEmpty()) {
            return Collections.unmodifiableList(dishes);
        }
        long id = merchantId.get();
        return dishes.stream().filter(d -> d.merchantId() == id).collect(Collectors.toUnmodifiableList());
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public long createOrder(long merchantId, int items) {
        Order order = new Order(orderId.getAndIncrement(), merchantId, items, "CREATED");
        orders.add(order);
        return order.id();
    }
}
