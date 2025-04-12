package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.SparkChatRequest;
import com.liuxy.campushub.dto.SparkChatResponse;

public interface SparkChatService {
    /**
     * 发送聊天请求
     *
     * @param request 请求参数
     * @return 响应结果
     */
    SparkChatResponse chat(SparkChatRequest request);
} 