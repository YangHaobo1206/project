package com.example.fooddelivery.controller;

import com.example.fooddelivery.common.Result;
import com.example.fooddelivery.entity.Merchant;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.MerchantService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Resource
    private MerchantService merchantService;

    @PostMapping("/apply")
    public Result<Merchant> apply(@RequestBody Merchant merchant) {
        return Result.success(merchantService.apply(merchant));
    }

    @GetMapping("/me")
    public Result<Merchant> myMerchant() {
        return Result.success(merchantService.myMerchant());
    }

    @PutMapping("/me")
    public Result<Merchant> update(@RequestBody Merchant merchant) {
        return Result.success(merchantService.updateMyMerchant(merchant));
    }

    @GetMapping
    public Result<java.util.List<Merchant>> approved() {
        return Result.success(merchantService.approvedMerchants());
    }
}
