package com.food.delivery.store;

public record Order(long id, long merchantId, int items, String status) {
}
