package com.liuxy.campushub.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ChatResponse {
    private boolean success;
    private String message;
    private String sessionId;
    
    public static ChatResponse success(String message, String sessionId) {
        return ChatResponse.builder()
                .success(true)
                .message(message)
                .sessionId(sessionId)
                .build();
    }
    
    public static ChatResponse error(String message) {
        return ChatResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
} 