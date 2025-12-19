package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Carousel;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.entity.User;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.repository.CarouselRepository;
import com.example.fooddelivery.repository.DishRepository;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.repository.UserRepository;
import com.example.fooddelivery.service.CarouselService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    private final CarouselRepository carouselRepository;
    private final ShopRepository shopRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    public CarouselServiceImpl(CarouselRepository carouselRepository,
                               ShopRepository shopRepository,
                               DishRepository dishRepository,
                               UserRepository userRepository) {
        this.carouselRepository = carouselRepository;
        this.shopRepository = shopRepository;
        this.dishRepository = dishRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Carousel> listPublic() {
        return carouselRepository.findTop5ByStatusOrderBySortOrderAscIdAsc("APPROVED");
    }

    @Override
    public List<Carousel> listMine(Long ownerId) {
        return carouselRepository.findByShopOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Carousel apply(Long shopId, Long dishId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(ownerId)) {
            throw new BusinessException(403, "Only allowed to submit carousel for your own shop");
        }
        if (!"APPROVED".equalsIgnoreCase(shop.getStatus())) {
            throw new BusinessException(400, "Shop not approved, cannot apply carousel");
        }
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new BusinessException(404, "Dish not found"));
        if (!dish.getShop().getId().equals(shopId)) {
            throw new BusinessException(400, "Dish does not belong to this shop");
        }

        Carousel existing = carouselRepository.findFirstByShop_IdAndDish_Id(shopId, dishId).orElse(null);
        if (existing != null) {
            String status = existing.getStatus() == null ? "" : existing.getStatus().trim().toUpperCase();
            if ("REJECTED".equals(status) || "STOPPED".equals(status)) {
                existing.setStatus("PENDING");
                existing.setReviewer(null);
                existing.setReviewedAt(null);
                existing.setImageUrl(dish.getImageUrl());
                existing.setUpdatedAt(LocalDateTime.now());
                return carouselRepository.save(existing);
            }
            if ("PENDING".equals(status) || "APPROVED".equals(status)) {
                throw new BusinessException(400, "This dish has already been submitted or is currently playing");
            }
            // For any other historical status, re-queue to avoid unique constraint collisions
            existing.setStatus("PENDING");
            existing.setReviewer(null);
            existing.setReviewedAt(null);
            existing.setImageUrl(dish.getImageUrl());
            existing.setUpdatedAt(LocalDateTime.now());
            return carouselRepository.save(existing);
        }

        // Prevent duplicate submissions or already playing
        carouselRepository.findFirstByDishIdAndStatusIn(dishId, Arrays.asList("PENDING", "APPROVED"))
                .ifPresent(c -> {
                    throw new BusinessException(400, "This dish has already been submitted or is currently playing");
                });
        Carousel c = new Carousel();
        c.setShop(shop);
        c.setDish(dish);
        c.setImageUrl(dish.getImageUrl());
        c.setStatus("PENDING");
        c.setSortOrder(0);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return carouselRepository.save(c);
    }

    @Override
    public List<Carousel> listAll() {
        return carouselRepository.findAll();
    }

    @Override
    @Transactional
    public Carousel approve(Long id, Long reviewerId, int maxPlaying) {
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        if (carouselRepository.countByStatus("APPROVED") >= maxPlaying) {
            throw new RuntimeException("Carousel playing limit reached");
        }
        User reviewer = reviewerId == null ? null : userRepository.findById(reviewerId).orElse(null);
        c.setStatus("APPROVED");
        c.setReviewer(reviewer);
        c.setReviewedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return carouselRepository.save(c);
    }

    @Override
    @Transactional
    public Carousel reject(Long id, Long reviewerId) {
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        User reviewer = reviewerId == null ? null : userRepository.findById(reviewerId).orElse(null);
        c.setStatus("REJECTED");
        c.setReviewer(reviewer);
        c.setReviewedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return carouselRepository.save(c);
    }

    @Override
    @Transactional
    public Carousel stop(Long id, Long reviewerId) {
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        User reviewer = reviewerId == null ? null : userRepository.findById(reviewerId).orElse(null);
        c.setStatus("STOPPED");
        c.setReviewer(reviewer);
        c.setReviewedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return carouselRepository.save(c);
    }
}
