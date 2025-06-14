package com.liuxy.campushub.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 角色：user 或 assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 创建用户消息
     */
    public static ChatMessage createUserMessage(String content) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(java.util.UUID.randomUUID().toString());
        message.setRole("user");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    /**
     * 创建助手消息
     */
    public static ChatMessage createAssistantMessage(String content) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(java.util.UUID.randomUUID().toString());
        message.setRole("assistant");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
} 