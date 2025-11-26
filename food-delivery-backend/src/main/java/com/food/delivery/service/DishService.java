package com.food.delivery.service;

import com.food.delivery.entity.Dish;

import java.util.List;

public interface DishService {
    void addDish(Dish dish);
    void updateDish(Dish dish);
    void deleteDish(Long id);
    List<Dish> listByMerchant(Long merchantId);
    List<Dish> listOnSaleByMerchant(Long merchantId);
    Dish getById(Long id);
}
