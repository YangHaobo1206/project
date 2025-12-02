package com.example.fooddelivery.repository;

import com.example.fooddelivery.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
