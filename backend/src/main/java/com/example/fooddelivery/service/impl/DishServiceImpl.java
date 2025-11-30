package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.repository.DishRepository;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final ShopRepository shopRepository;

    public DishServiceImpl(DishRepository dishRepository, ShopRepository shopRepository) {
        this.dishRepository = dishRepository;
        this.shopRepository = shopRepository;
    }

    @Override
    public Dish create(Dish dish, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        dish.setShop(shop);
        dish.setAvailable(true);
        return dishRepository.save(dish);
    }

    @Override
    public Dish update(Long id, Dish dish, Long shopId) {
        Dish existing = dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        existing.setName(dish.getName());
        existing.setCategory(dish.getCategory());
        existing.setPrice(dish.getPrice());
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
            existing.setShop(shop);
        }
        return dishRepository.save(existing);
    }

    @Override
    public Dish toggleAvailability(Long id, boolean available) {
        Dish dish = dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        dish.setAvailable(available);
        return dishRepository.save(dish);
    }

    @Override
    public List<Dish> listByShop(Long shopId) {
        return dishRepository.findByShopId(shopId);
    }

    @Override
    public List<Dish> listAll() {
        return dishRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new BusinessException(404, "Dish not found");
        }
        dishRepository.deleteById(id);
    }
}
