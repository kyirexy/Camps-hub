package com.liuxy.campushub.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxy.campushub.entity.Weather;
import com.liuxy.campushub.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * 天气服务实现类
 */
@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${weather.api.id}")
    private String apiId;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Override
    public Weather getWeather(String province, String city) {
        try {
            String url = String.format("%s?id=%s&key=%s&sheng=%s&place=%s",
                    apiUrl, apiId, apiKey, province, city);
            
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readValue(response, Weather.class);
        } catch (Exception e) {
            log.error("获取天气信息失败", e);
            Weather weather = new Weather();
            weather.setCode(400);
            weather.setMsg("获取天气信息失败：" + e.getMessage());
            return weather;
        }
    }
} 