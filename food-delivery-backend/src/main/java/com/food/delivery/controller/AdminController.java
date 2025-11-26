package com.food.delivery.controller;

import com.food.delivery.common.response.Result;
import com.food.delivery.entity.Merchant;
import com.food.delivery.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MerchantService merchantService;

    public AdminController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // 待审核商家列表
    @GetMapping("/merchant/pending")
    public Result<List<Merchant>> pendingMerchants() {
        return Result.success(merchantService.listPending());
    }

    // 审核商家
    @PostMapping("/merchant/{id}/approve")
    public Result<?> approve(@PathVariable Long id,
                             @RequestParam Boolean pass) {
        merchantService.approve(id, pass);
        return Result.success();
    }

    // 这里可以继续扩展用户管理、数据统计等接口
}
