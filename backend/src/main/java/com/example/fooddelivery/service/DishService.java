package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Dish;

import java.util.List;

public interface DishService {
    Dish create(Dish dish, Long shopId);
    Dish update(Long id, Dish dish);
    Dish toggleAvailability(Long id, boolean available);
    List<Dish> listByShop(Long shopId);
}
