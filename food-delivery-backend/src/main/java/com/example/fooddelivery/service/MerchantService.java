package com.example.fooddelivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.fooddelivery.entity.Merchant;

import java.util.List;

public interface MerchantService extends IService<Merchant> {
    Merchant apply(Merchant merchant);

    Merchant myMerchant();

    Merchant updateMyMerchant(Merchant merchant);

    List<Merchant> pendingMerchants();

    void approve(Long id, boolean pass);

    List<Merchant> approvedMerchants();
}
