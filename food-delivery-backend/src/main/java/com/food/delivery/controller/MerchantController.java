package com.food.delivery.controller;

import com.food.delivery.common.jwt.LoginUser;
import com.food.delivery.common.response.Result;
import com.food.delivery.entity.Merchant;
import com.food.delivery.service.MerchantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // 商家入驻申请
    @PostMapping("/apply")
    public Result<?> apply(@RequestBody Merchant merchant) {
        Long uid = LoginUser.get().getId();
        merchant.setOwnerUserId(uid);
        merchantService.apply(merchant);
        return Result.success();
    }

    // 当前登录用户的店铺信息
    @GetMapping("/me")
    public Result<Merchant> me() {
        Long uid = LoginUser.get().getId();
        return Result.success(merchantService.getByOwner(uid));
    }

    // 更新店铺
    @PutMapping("/me")
    public Result<?> update(@RequestBody Merchant merchant) {
        Long uid = LoginUser.get().getId();
        merchantService.updateByOwner(uid, merchant);
        return Result.success();
    }
}
