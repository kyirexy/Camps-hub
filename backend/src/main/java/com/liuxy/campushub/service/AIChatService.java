package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.ChatRequest;
import com.liuxy.campushub.dto.ChatResponse;

public interface AIChatService {
    ChatResponse chat(ChatRequest request);
} 