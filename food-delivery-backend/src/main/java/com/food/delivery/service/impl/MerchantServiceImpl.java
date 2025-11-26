package com.food.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.food.delivery.common.exception.BizException;
import com.food.delivery.entity.Merchant;
import com.food.delivery.mapper.MerchantMapper;
import com.food.delivery.service.MerchantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;

    public MerchantServiceImpl(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    @Override
    public void apply(Merchant merchant) {
        merchant.setApproved(false);
        merchantMapper.insert(merchant);
    }

    @Override
    public Merchant getByOwner(Long ownerUserId) {
        return merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getOwnerUserId, ownerUserId));
    }

    @Override
    public void updateByOwner(Long ownerUserId, Merchant merchant) {
        Merchant db = getByOwner(ownerUserId);
        if (db == null) {
            throw new BizException("未找到商家信息");
        }
        merchant.setId(db.getId());
        merchant.setOwnerUserId(ownerUserId);
        merchantMapper.updateById(merchant);
    }

    @Override
    public List<Merchant> listPending() {
        return merchantMapper.selectList(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getApproved, false));
    }

    @Override
    public void approve(Long id, boolean pass) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new BizException("商家不存在");
        }
        merchant.setApproved(pass);
        merchantMapper.updateById(merchant);
    }
}
