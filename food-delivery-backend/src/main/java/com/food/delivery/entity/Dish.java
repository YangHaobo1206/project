package com.food.delivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_dish")
public class Dish {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;
    private String name;
    private String image;
    private String category;
    private BigDecimal price;
    private Boolean onSale;
    private String description;
}
