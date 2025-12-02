package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Shop;

import java.util.List;

public interface ShopService {
    Shop create(Shop shop);
    Shop update(Long id, Shop shop);
    void delete(Long id);
    List<Shop> findAll();
    Shop toggleOnline(Long id, boolean online);
}
