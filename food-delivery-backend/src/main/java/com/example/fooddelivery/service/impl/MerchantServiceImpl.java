package com.example.fooddelivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.entity.Merchant;
import com.example.fooddelivery.mapper.MerchantMapper;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.MerchantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Override
    public Merchant apply(Merchant merchant) {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");
        merchant.setId(null);
        merchant.setUserId(session.userId());
        merchant.setStatus("PENDING");
        save(merchant);
        return merchant;
    }

    @Override
    public Merchant myMerchant() {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");
        return lambdaQuery().eq(Merchant::getUserId, session.userId()).one();
    }

    @Override
    public Merchant updateMyMerchant(Merchant merchant) {
        Merchant exist = myMerchant();
        if (exist == null) throw new BizException(404, "merchant not found");
        exist.setName(merchant.getName());
        exist.setDescription(merchant.getDescription());
        exist.setAddress(merchant.getAddress());
        updateById(exist);
        return exist;
    }

    @Override
    public List<Merchant> pendingMerchants() {
        return lambdaQuery().eq(Merchant::getStatus, "PENDING").list();
    }

    @Override
    public void approve(Long id, boolean pass) {
        Merchant merchant = getById(id);
        if (merchant == null) throw new BizException(404, "merchant not found");
        merchant.setStatus(pass ? "APPROVED" : "REJECTED");
        updateById(merchant);
    }

    @Override
    public List<Merchant> approvedMerchants() {
        return lambdaQuery().eq(Merchant::getStatus, "APPROVED").list();
    }
}
