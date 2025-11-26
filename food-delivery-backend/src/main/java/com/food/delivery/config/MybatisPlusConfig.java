package com.food.delivery.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.food.delivery.mapper")
public class MybatisPlusConfig {
}
