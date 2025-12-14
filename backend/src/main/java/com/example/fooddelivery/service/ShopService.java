package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Shop;

import java.util.List;

public interface ShopService {
    Shop create(Shop shop);
    Shop createForMerchant(Shop shop, Long ownerId);
    Shop approve(Long id);
    Shop reject(Long id);
    Shop update(Long id, Shop shop);
    void delete(Long id);
    List<Shop> findAll();
    List<Shop> findByOwner(Long ownerId);
    Shop findById(Long id);
    Shop toggleOnline(Long id, boolean online, Long operatorId, boolean operatorIsAdmin);
}
