package com.example.fooddelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String phone;
    private String address;
    /**
     * USER 或 MERCHANT，默认 USER
     */
    private String role;
    private String shopName;
    private String shopDescription;
    private String shopAddress;
}
