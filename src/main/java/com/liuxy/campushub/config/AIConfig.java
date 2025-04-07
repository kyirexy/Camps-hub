package com.liuxy.campushub.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

@Configuration
public class AIConfig {
    
    @Value("${ai.api.appid}")
    private String appId;

    @Value("${ai.api.secret}")
    private String apiSecret;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.spark.url}")
    private String sparkUrl;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public JSONObject sparkConfig() {
        JSONObject config = new JSONObject();
        config.put("appId", appId);
        config.put("apiSecret", apiSecret);
        config.put("apiKey", apiKey);
        config.put("url", sparkUrl);
        return config;
    }
} 