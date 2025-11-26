package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Merchant;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.DishService;
import com.example.fooddelivery.service.MerchantService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private MerchantService merchantService;

    @GetMapping("/list/{merchantId}")
    public Result<List<Dish>> listOnSale(@PathVariable Long merchantId) {
        return Result.success(dishService.listByMerchant(merchantId, true));
    }

    @GetMapping("/merchant/{merchantId}")
    public Result<List<Dish>> listAll(@PathVariable Long merchantId) {
        return Result.success(dishService.listByMerchant(merchantId, false));
    }

    @PostMapping
    public Result<Dish> create(@RequestBody Dish dish) {
        return Result.success(dishService.create(dish));
    }

    @PutMapping
    public Result<Dish> update(@RequestBody Dish dish) {
        return Result.success(dishService.updateDish(dish));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success();
    }
}
