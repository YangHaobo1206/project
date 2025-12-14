package com.example.fooddelivery.controller;

import com.example.fooddelivery.dto.ApiResponse;
import com.example.fooddelivery.dto.DashboardResponse;
import com.example.fooddelivery.service.DashboardService;
import com.example.fooddelivery.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardResponse> admin() {
        return ApiResponse.success(dashboardService.getAdminDashboard());
    }

    @GetMapping("/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ApiResponse<DashboardResponse> merchant(Authentication authentication) {
        Long uid = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ApiResponse.success(dashboardService.getMerchantDashboard(uid));
    }
}
