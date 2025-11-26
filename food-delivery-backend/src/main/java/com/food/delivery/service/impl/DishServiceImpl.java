package com.food.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.food.delivery.entity.Dish;
import com.food.delivery.mapper.DishMapper;
import com.food.delivery.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;

    public DishServiceImpl(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    @Override
    public void addDish(Dish dish) {
        dishMapper.insert(dish);
    }

    @Override
    public void updateDish(Dish dish) {
        dishMapper.updateById(dish);
    }

    @Override
    public void deleteDish(Long id) {
        dishMapper.deleteById(id);
    }

    @Override
    public List<Dish> listByMerchant(Long merchantId) {
        return dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getMerchantId, merchantId));
    }

    @Override
    public List<Dish> listOnSaleByMerchant(Long merchantId) {
        return dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getOnSale, true));
    }

    @Override
    public Dish getById(Long id) {
        return dishMapper.selectById(id);
    }
}
