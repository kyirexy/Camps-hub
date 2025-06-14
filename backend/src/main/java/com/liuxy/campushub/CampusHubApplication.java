package com.liuxy.campushub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.liuxy.campushub.mapper")
public class CampusHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusHubApplication.class, args);
    }

}
