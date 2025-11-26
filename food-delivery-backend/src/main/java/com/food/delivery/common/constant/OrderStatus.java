package com.food.delivery.common.constant;

public enum OrderStatus {
    PENDING_PAYMENT,   // 待支付
    PENDING_ACCEPT,    // 待接单
    DELIVERING,        // 配送中
    COMPLETED,         // 已完成
    CANCELED           // 已取消
}
