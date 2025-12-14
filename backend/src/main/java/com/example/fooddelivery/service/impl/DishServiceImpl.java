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
    public Dish create(Dish dish, Long shopId, Long operatorId, boolean operatorIsAdmin) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        validatePermission(shop, operatorId, operatorIsAdmin);
        dish.setShop(shop);
        dish.setAvailable(true);
        if (dish.getImageUrl() == null) {
            dish.setImageUrl("");
        }
        return dishRepository.save(dish);
    }

    @Override
    public Dish update(Long id, Dish dish, Long shopId, Long operatorId, boolean operatorIsAdmin) {
        Dish existing = dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        validatePermission(existing.getShop(), operatorId, operatorIsAdmin);
        existing.setName(dish.getName());
        existing.setCategory(dish.getCategory());
        existing.setPrice(dish.getPrice());
        if (dish.getImageUrl() != null) {
            existing.setImageUrl(dish.getImageUrl());
        }
        if (shopId != null) {
            Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
            validatePermission(shop, operatorId, operatorIsAdmin);
            existing.setShop(shop);
        }
        return dishRepository.save(existing);
    }

    @Override
    public Dish toggleAvailability(Long id, boolean available, Long operatorId, boolean operatorIsAdmin) {
        Dish dish = dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        validatePermission(dish.getShop(), operatorId, operatorIsAdmin);
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
    public void delete(Long id, Long operatorId, boolean operatorIsAdmin) {
        Dish dish = dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        validatePermission(dish.getShop(), operatorId, operatorIsAdmin);
        dishRepository.delete(dish);
    }

    @Override
    public Dish findById(Long id) {
        return dishRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Dish not found"));
    }

    private void validatePermission(Shop shop, Long operatorId, boolean operatorIsAdmin) {
        if (shop == null) {
            throw new BusinessException(404, "Shop not found");
        }
        if (!"APPROVED".equals(shop.getStatus())) {
            throw new BusinessException(400, "店铺未通过审核，无法操作菜品");
        }
        if (operatorIsAdmin) {
            return;
        }
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(operatorId)) {
            throw new BusinessException(403, "无权限操作该店铺菜品");
        }
    }
}
