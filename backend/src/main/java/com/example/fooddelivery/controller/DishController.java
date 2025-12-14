package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.service.DishService;
import com.example.fooddelivery.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;
    private final UserService userService;
    @Value("${app.upload-dir:uploads/images}")
    private String uploadDir;

    public DishController(DishService dishService, UserService userService) {
        this.dishService = dishService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Dish> create(@RequestParam Long shopId, @RequestBody Dish dish, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(dishService.create(dish, shopId, uid, isAdmin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Dish> update(@PathVariable Long id,
                                    @RequestParam(required = false) Long shopId,
                                    @RequestBody Dish dish,
                                    Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(dishService.update(id, dish, shopId, uid, isAdmin));
    }

    @PostMapping("/{id}/on")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Dish> onSale(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(dishService.toggleAvailability(id, true, uid, isAdmin));
    }

    @PostMapping("/{id}/off")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Dish> offSale(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ApiResponse.success(dishService.toggleAvailability(id, false, uid, isAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        dishService.delete(id, uid, isAdmin);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN','MERCHANT')")
    public ApiResponse<String> uploadImage(@PathVariable Long id,
                                           @RequestParam("file") MultipartFile file,
                                           Authentication authentication) throws Exception {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Dish dish = dishService.findById(id);
        if (!isAdmin && (dish.getShop() == null || dish.getShop().getOwner() == null || !dish.getShop().getOwner().getId().equals(uid))) {
            throw new RuntimeException("无权限上传该菜品图片");
        }
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        Files.createDirectories(Paths.get(uploadDir));
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = "dish_" + id + "_" + Instant.now().toEpochMilli() + ext;
        Path target = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), target);
        String url = "/images/" + filename;
        dish.setImageUrl(url);
        dishService.update(dish.getId(), dish, dish.getShop() != null ? dish.getShop().getId() : null, uid, isAdmin);
        return ApiResponse.success(url);
    }

    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<Dish>> listByShop(@PathVariable Long shopId) {
        return ApiResponse.success(dishService.listByShop(shopId));
    }

    @GetMapping
    public ApiResponse<List<Dish>> listAll() {
        return ApiResponse.success(dishService.listAll());
    }
}
