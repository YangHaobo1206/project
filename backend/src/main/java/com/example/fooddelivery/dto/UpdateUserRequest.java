package com.example.fooddelivery.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String phone;
    private String role;
    private String address;
}
