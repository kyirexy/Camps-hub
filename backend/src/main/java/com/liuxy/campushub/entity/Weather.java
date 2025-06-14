package com.liuxy.campushub.entity;

import lombok.Data;

/**
 * 天气信息实体类
 */
@Data
public class Weather {
    /**
     * 降水量
     */
    private Double precipitation;
    
    /**
     * 温度
     */
    private Double temperature;
    
    /**
     * 气压
     */
    private Integer pressure;
    
    /**
     * 湿度
     */
    private Integer humidity;
    
    /**
     * 风向
     */
    private String windDirection;
    
    /**
     * 风向角度
     */
    private Integer windDirectionDegree;
    
    /**
     * 风速
     */
    private Double windSpeed;
    
    /**
     * 风力等级
     */
    private String windScale;
    
    /**
     * 地区
     */
    private String place;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 错误信息
     */
    private String msg;
} 