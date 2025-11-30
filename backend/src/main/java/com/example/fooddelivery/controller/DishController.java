package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.service.DishService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Dish> create(@RequestParam Long shopId, @RequestBody Dish dish) {
        return ApiResponse.success(dishService.create(dish, shopId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Dish> update(@PathVariable Long id,
                                    @RequestParam(required = false) Long shopId,
                                    @RequestBody Dish dish) {
        return ApiResponse.success(dishService.update(id, dish, shopId));
    }

    @PostMapping("/{id}/on")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Dish> onSale(@PathVariable Long id) {
        return ApiResponse.success(dishService.toggleAvailability(id, true));
    }

    @PostMapping("/{id}/off")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Dish> offSale(@PathVariable Long id) {
        return ApiResponse.success(dishService.toggleAvailability(id, false));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<Dish>> listByShop(@PathVariable Long shopId) {
        return ApiResponse.success(dishService.listByShop(shopId));
    }

    @GetMapping
    public ApiResponse<List<Dish>> listAll() {
        return ApiResponse.success(dishService.listAll());
    }
}
