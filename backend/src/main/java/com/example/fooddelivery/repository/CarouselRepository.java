package com.example.fooddelivery.repository;

import com.example.fooddelivery.entity.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarouselRepository extends JpaRepository<Carousel, Long> {
    List<Carousel> findByShopOwnerId(Long ownerId);

    List<Carousel> findByStatusOrderBySortOrderAscIdAsc(String status);

    List<Carousel> findTop5ByStatusOrderBySortOrderAscIdAsc(String status);

    long countByStatus(String status);

    Optional<Carousel> findFirstByDishIdAndStatusIn(Long dishId, List<String> statuses);

    Optional<Carousel> findFirstByShop_IdAndDish_Id(Long shopId, Long dishId);
}
