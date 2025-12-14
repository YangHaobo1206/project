package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Carousel;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.entity.User;
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
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new RuntimeException("店铺不存在"));
        if (shop.getOwner() == null || !shop.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("只能提交自己店铺的菜品");
        }
        if (!"APPROVED".equalsIgnoreCase(shop.getStatus())) {
            throw new RuntimeException("店铺未通过审核，无法申请轮播");
        }
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new RuntimeException("菜品不存在"));
        if (!dish.getShop().getId().equals(shopId)) {
            throw new RuntimeException("菜品不属于该店铺");
        }
        // 防止重复申请或重复通过
        carouselRepository.findFirstByDishIdAndStatusIn(dishId, Arrays.asList("PENDING", "APPROVED"))
                .ifPresent(c -> {
                    throw new RuntimeException("该菜品已提交或正在播放");
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
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("记录不存在"));
        if (carouselRepository.countByStatus("APPROVED") >= maxPlaying) {
            throw new RuntimeException("播放条数已达上限");
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
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("记录不存在"));
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
        Carousel c = carouselRepository.findById(id).orElseThrow(() -> new RuntimeException("记录不存在"));
        User reviewer = reviewerId == null ? null : userRepository.findById(reviewerId).orElse(null);
        c.setStatus("STOPPED");
        c.setReviewer(reviewer);
        c.setReviewedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return carouselRepository.save(c);
    }
}
