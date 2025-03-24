package com.liuxy.campushub.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.liuxy.campushub.mapper")
public class MyBatisConfig {
} 