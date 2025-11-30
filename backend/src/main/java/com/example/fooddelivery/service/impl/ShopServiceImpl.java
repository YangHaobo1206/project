package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.service.ShopService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    public ShopServiceImpl(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Override
    public Shop create(Shop shop) {
        shop.setOnline(true);
        return shopRepository.save(shop);
    }

    @Override
    public Shop update(Long id, Shop shop) {
        Shop existing = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        existing.setName(shop.getName());
        existing.setDescription(shop.getDescription());
        existing.setAddress(shop.getAddress());
        return shopRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        shopRepository.deleteById(id);
    }

    @Override
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Override
    public Shop toggleOnline(Long id, boolean online) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        shop.setOnline(online);
        return shopRepository.save(shop);
    }
}
