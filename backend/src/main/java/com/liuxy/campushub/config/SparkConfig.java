package com.liuxy.campushub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spark.chat")
public class SparkConfig {
    /**
     * 应用ID
     */
    private String appId;
    
    /**
     * 应用密钥
     */
    private String secret;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 知识库ID
     */
    private String repoId;
    
    /**
     * 文件ID列表
     */
    private String[] fileIds;
    
    /**
     * 向量库文本段查询数量
     */
    private Integer topN = 5;
    
    /**
     * 检索过滤级别
     */
    private String retrievalFilterPolicy = "REGULAR";
    
    /**
     * 大模型问答温度
     */
    private Double temperature = 0.5;
    
    /**
     * 问答模式
     */
    private String qaMode = "MIX";
    
    /**
     * WebSocket URL
     */
    private String websocketUrl = "wss://spark-api.xf-yun.com/v4.0/chat";
    
    /**
     * HTTP URL
     */
    private String httpUrl = "https://spark-api-open.xf-yun.com/v1/chat/completions";
} 