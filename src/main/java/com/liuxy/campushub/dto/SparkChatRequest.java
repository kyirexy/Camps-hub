package com.liuxy.campushub.dto;

import lombok.Data;
import java.util.List;

@Data
public class SparkChatRequest {
    /**
     * 问题内容
     */
    private String question;
    
    /**
     * 会话历史
     */
    private List<ChatMessage> history;

    @Data
    public static class ChatMessage {
        /**
         * 角色：user 或 assistant
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
    }
} 