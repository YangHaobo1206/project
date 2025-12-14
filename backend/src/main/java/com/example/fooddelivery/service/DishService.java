package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Dish;

import java.util.List;

public interface DishService {
    Dish create(Dish dish, Long shopId, Long operatorId, boolean operatorIsAdmin);
    Dish update(Long id, Dish dish, Long shopId, Long operatorId, boolean operatorIsAdmin);
    Dish toggleAvailability(Long id, boolean available, Long operatorId, boolean operatorIsAdmin);
    List<Dish> listByShop(Long shopId);
    List<Dish> listAll();
    void delete(Long id, Long operatorId, boolean operatorIsAdmin);
    Dish findById(Long id);
}
