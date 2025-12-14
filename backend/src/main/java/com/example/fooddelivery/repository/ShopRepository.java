package com.example.fooddelivery.repository;

import com.example.fooddelivery.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByOwnerId(Long ownerId);
    Optional<Shop> findFirstByOwnerId(Long ownerId);
}
