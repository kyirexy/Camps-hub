package com.liuxy.campushub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源处理，确保API请求不会被当作静态资源处理
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 添加调试日志
        System.out.println("WebConfig: 配置了静态资源处理器，API请求不会被当作静态资源处理");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 添加一个视图控制器，将/api/posts/重定向到/api/posts/list
        registry.addRedirectViewController("/api/posts/", "/api/posts/list");
        System.out.println("WebConfig: 添加了重定向视图控制器，将/api/posts/重定向到/api/posts/list");
    }
} 