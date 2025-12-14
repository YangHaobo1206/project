package com.example.fooddelivery.repository;

import com.example.fooddelivery.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByShopId(Long shopId);
    List<Dish> findByShopIdIn(Collection<Long> shopIds);
}
