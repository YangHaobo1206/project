package com.example.fooddelivery.service;

import com.example.fooddelivery.dto.DashboardResponse;

public interface DashboardService {
    DashboardResponse getAdminDashboard();
    DashboardResponse getMerchantDashboard(Long ownerId);
}
