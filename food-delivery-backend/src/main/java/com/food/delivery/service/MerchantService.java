package com.food.delivery.service;

import com.food.delivery.entity.Merchant;

import java.util.List;

public interface MerchantService {
    void apply(Merchant merchant);
    Merchant getByOwner(Long ownerUserId);
    void updateByOwner(Long ownerUserId, Merchant merchant);
    List<Merchant> listPending();
    void approve(Long id, boolean pass);
}
