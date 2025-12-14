package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Carousel;
import com.example.fooddelivery.service.CarouselService;
import com.example.fooddelivery.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carousels")
public class CarouselController {

    private final CarouselService carouselService;
    private final UserService userService;

    @Value("${app.carousel.max-playing:5}")
    private int maxPlaying;

    public CarouselController(CarouselService carouselService, UserService userService) {
        this.carouselService = carouselService;
        this.userService = userService;
    }

    /**
     * 用户端公开列表
     */
    @GetMapping("/public")
    public ApiResponse<List<Carousel>> publicList() {
        return ApiResponse.success(carouselService.listPublic());
    }

    /**
     * 商家查看自己的轮播申请
     */
    @GetMapping("/mine")
    @PreAuthorize("hasRole('MERCHANT')")
    public ApiResponse<List<Carousel>> mine(Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(carouselService.listMine(uid));
    }

    /**
     * 商家申请轮播
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('MERCHANT')")
    public ApiResponse<Carousel> apply(@RequestBody ApplyRequest req, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(carouselService.apply(req.getShopId(), req.getDishId(), uid));
    }

    /**
     * 管理端查看全部
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Carousel>> all() {
        return ApiResponse.success(carouselService.listAll());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Carousel> approve(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(carouselService.approve(id, uid, maxPlaying));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Carousel> reject(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(carouselService.reject(id, uid));
    }

    @PostMapping("/{id}/stop")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Carousel> stop(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ApiResponse.success(carouselService.stop(id, uid));
    }

    /**
     * 申请体
     */
    public static class ApplyRequest {
        private Long shopId;
        private Long dishId;

        public Long getShopId() {
            return shopId;
        }

        public void setShopId(Long shopId) {
            this.shopId = shopId;
        }

        public Long getDishId() {
            return dishId;
        }

        public void setDishId(Long dishId) {
            this.dishId = dishId;
        }
    }
}
