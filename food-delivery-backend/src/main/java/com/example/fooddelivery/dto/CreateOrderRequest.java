package com.example.fooddelivery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long merchantId;
    @NotEmpty
    private List<Item> items;

    @Data
    public static class Item {
        private Long dishId;
        private Integer quantity;
    }
}
