package com.example.fooddelivery.service.impl;

import com.example.fooddelivery.entity.Shop;
import com.example.fooddelivery.exception.BusinessException;
import com.example.fooddelivery.repository.ShopRepository;
import com.example.fooddelivery.service.ShopService;
import com.example.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ShopServiceImpl(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Shop create(Shop shop) {
        shop.setStatus("APPROVED");
        shop.setOnline(true);
        return shopRepository.save(shop);
    }

    @Override
    public Shop createForMerchant(Shop shop, Long ownerId) {
        var owner = userRepository.findById(ownerId).orElseThrow(() -> new BusinessException(404, "Owner not found"));
        shop.setOwner(owner);
        shop.setStatus("PENDING");
        shop.setOnline(false);
        return shopRepository.save(shop);
    }

    @Override
    public Shop approve(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        shop.setStatus("APPROVED");
        return shopRepository.save(shop);
    }

    @Override
    public Shop reject(Long id) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        shop.setStatus("REJECTED");
        shop.setOnline(false);
        return shopRepository.save(shop);
    }

    @Override
    public Shop update(Long id, Shop shop) {
        Shop existing = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        existing.setName(shop.getName());
        existing.setDescription(shop.getDescription());
        existing.setAddress(shop.getAddress());
        return shopRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        shopRepository.deleteById(id);
    }

    @Override
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Override
    public List<Shop> findByOwner(Long ownerId) {
        return shopRepository.findByOwnerId(ownerId);
    }

    @Override
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
    }

    @Override
    public Shop toggleOnline(Long id, boolean online, Long operatorId, boolean operatorIsAdmin) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(404, "Shop not found"));
        if (!"APPROVED".equals(shop.getStatus())) {
            throw new BusinessException(400, "店铺未通过审核，无法变更营业状态");
        }
        if (!operatorIsAdmin && (shop.getOwner() == null || !shop.getOwner().getId().equals(operatorId))) {
            throw new BusinessException(403, "无权限修改该店铺");
        }
        shop.setOnline(online);
        return shopRepository.save(shop);
    }
}
