package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Weather;

/**
 * 天气服务接口
 */
public interface WeatherService {
    
    /**
     * 获取指定地区的天气信息
     *
     * @param province 省份名称
     * @param city 城市名称
     * @return 天气信息
     */
    Weather getWeather(String province, String city);
} 