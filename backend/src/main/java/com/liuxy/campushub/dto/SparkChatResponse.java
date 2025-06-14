package com.liuxy.campushub.dto;

import lombok.Data;
import java.util.List;

@Data
public class SparkChatResponse {
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误信息
     */
    private String message;
    
    /**
     * 回答内容
     */
    private String content;
    
    /**
     * 会话状态
     */
    private Integer status;
    
    /**
     * 文档引用
     */
    private String fileRefer;
    
    /**
     * 获取文档引用Map
     */
    public java.util.Map<String, int[]> getFileReferMap() {
        if (fileRefer == null || fileRefer.isEmpty()) {
            return null;
        }
        try {
            return com.alibaba.fastjson.JSON.parseObject(fileRefer, java.util.Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Source> sources;

    @Data
    public static class Source {
        private String title;
        private String content;
    }
} 