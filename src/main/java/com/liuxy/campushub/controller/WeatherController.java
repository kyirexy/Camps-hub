package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.Weather;
import com.liuxy.campushub.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 天气控制器
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Resource
    private WeatherService weatherService;

    @GetMapping("/query")
    @PreAuthorize("permitAll()")
    public Weather getWeather(@RequestParam String province, @RequestParam String city) {
        return weatherService.getWeather(province, city);
    }
} 