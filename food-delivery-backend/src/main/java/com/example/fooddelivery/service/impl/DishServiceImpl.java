package com.example.fooddelivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.entity.Dish;
import com.example.fooddelivery.entity.Merchant;
import com.example.fooddelivery.mapper.DishMapper;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.service.DishService;
import com.example.fooddelivery.service.MerchantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private MerchantService merchantService;

    @Override
    public Dish create(Dish dish) {
        Merchant merchant = validateMerchantOwner();
        dish.setMerchantId(merchant.getId());
        dish.setStatus("ON");
        save(dish);
        return dish;
    }

    @Override
    public Dish updateDish(Dish dish) {
        Merchant merchant = validateMerchantOwner();
        Dish exist = getById(dish.getId());
        if (exist == null || !exist.getMerchantId().equals(merchant.getId())) {
            throw new BizException(404, "dish not found");
        }
        exist.setName(dish.getName());
        exist.setDescription(dish.getDescription());
        exist.setPrice(dish.getPrice());
        exist.setCategory(dish.getCategory());
        exist.setStatus(dish.getStatus());
        updateById(exist);
        return exist;
    }

    @Override
    public void deleteDish(Long id) {
        Merchant merchant = validateMerchantOwner();
        Dish exist = getById(id);
        if (exist == null || !exist.getMerchantId().equals(merchant.getId())) {
            throw new BizException(404, "dish not found");
        }
        removeById(id);
    }

    @Override
    public List<Dish> listByMerchant(Long merchantId, boolean onlyOnSale) {
        return lambdaQuery()
                .eq(Dish::getMerchantId, merchantId)
                .eq(onlyOnSale, Dish::getStatus, "ON")
                .list();
    }

    private Merchant validateMerchantOwner() {
        AuthContext.UserSession session = AuthContext.get();
        if (session == null) throw new BizException(401, "Unauthorized");
        Merchant merchant = merchantService.myMerchant();
        if (merchant == null || !"APPROVED".equals(merchant.getStatus())) {
            throw new BizException(403, "merchant not approved");
        }
        return merchant;
    }
}
