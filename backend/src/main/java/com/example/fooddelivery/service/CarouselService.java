package com.example.fooddelivery.service;

import com.example.fooddelivery.entity.Carousel;

import java.util.List;

public interface CarouselService {
    List<Carousel> listPublic();

    List<Carousel> listMine(Long ownerId);

    Carousel apply(Long shopId, Long dishId, Long ownerId);

    List<Carousel> listAll();

    Carousel approve(Long id, Long reviewerId, int maxPlaying);

    Carousel reject(Long id, Long reviewerId);

    Carousel stop(Long id, Long reviewerId);
}
