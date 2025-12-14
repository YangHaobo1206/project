package com.example.fooddelivery.repository;

import com.example.fooddelivery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByShopId(Long shopId);
    List<Order> findByUserId(Long userId);
    List<Order> findByShopIdIn(Collection<Long> shopIds);

    @Query("select o from Order o where o.shop.owner.id = :ownerId")
    List<Order> findByShopOwnerId(@Param("ownerId") Long ownerId);
}
