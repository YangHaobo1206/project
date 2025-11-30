package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/shop/{shopId}")
    public ApiResponse<Dish> create(@PathVariable Long shopId, @RequestBody Dish dish) {
        return ApiResponse.success(dishService.create(dish, shopId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Dish> update(@PathVariable Long id, @RequestBody Dish dish) {
        return ApiResponse.success(dishService.update(id, dish));
    }

    @PostMapping("/{id}/on")
    public ApiResponse<Dish> onSale(@PathVariable Long id) {
        return ApiResponse.success(dishService.toggleAvailability(id, true));
    }

    @PostMapping("/{id}/off")
    public ApiResponse<Dish> offSale(@PathVariable Long id) {
        return ApiResponse.success(dishService.toggleAvailability(id, false));
    }

    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<Dish>> listByShop(@PathVariable Long shopId) {
        return ApiResponse.success(dishService.listByShop(shopId));
    }
}
