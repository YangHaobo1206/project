package com.example.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    Dish create(Dish dish);

    Dish updateDish(Dish dish);

    void deleteDish(Long id);

    List<Dish> listByMerchant(Long merchantId, boolean onlyOnSale);
}
