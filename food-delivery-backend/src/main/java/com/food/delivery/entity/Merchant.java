package com.food.delivery.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String logo;
    private String address;
    private String phone;
    private String businessHours;
    private Boolean approved;
    private Long ownerUserId;
}
