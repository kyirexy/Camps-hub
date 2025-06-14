package com.liuxy.campushub.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ChatRequest {
    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
} 