package com.liuxy.campushub.controller;

import com.liuxy.campushub.dto.ChatRequest;
import com.liuxy.campushub.dto.ChatResponse;
import com.liuxy.campushub.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/ai")
public class AIChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);
    
    @Autowired
    private AIChatService aiChatService;
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        logger.info("收到AI聊天请求: {}", request.getMessage());
        try {
            ChatResponse response = aiChatService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("AI聊天请求处理失败", e);
            return ResponseEntity.internalServerError()
                .body(ChatResponse.error("处理请求失败: " + e.getMessage()));
        }
    }
} 