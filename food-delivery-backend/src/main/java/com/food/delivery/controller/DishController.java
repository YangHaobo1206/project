package com.food.delivery.controller;

import com.food.delivery.common.jwt.LoginUser;
import com.food.delivery.common.response.Result;
import com.food.delivery.entity.Dish;
import com.food.delivery.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    // 用户端：商家菜品列表（只看上架）
    @GetMapping("/list/{merchantId}")
    public Result<List<Dish>> listOnSale(@PathVariable Long merchantId) {
        return Result.success(dishService.listOnSaleByMerchant(merchantId));
    }

    // 商家端：全部菜品列表
    @GetMapping("/merchant/{merchantId}")
    public Result<List<Dish>> listByMerchant(@PathVariable Long merchantId) {
        return Result.success(dishService.listByMerchant(merchantId));
    }

    // 商家新增菜品
    @PostMapping
    public Result<?> add(@RequestBody Dish dish) {
        Long uid = LoginUser.get().getId();
        // 此处可根据 uid 查 merchantId，此处简化为前端传 merchantId
        dishService.addDish(dish);
        return Result.success();
    }

    // 商家更新菜品
    @PutMapping
    public Result<?> update(@RequestBody Dish dish) {
        dishService.updateDish(dish);
        return Result.success();
    }

    // 商家删除菜品
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success();
    }

    // 菜品详情
    @GetMapping("/{id}")
    public Result<Dish> detail(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }
}
