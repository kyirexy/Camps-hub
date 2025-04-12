package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.config.SparkConfig;
import com.liuxy.campushub.dto.SparkChatRequest;
import com.liuxy.campushub.dto.SparkChatResponse;
import com.liuxy.campushub.handler.SparkWebSocketHandler;
import com.liuxy.campushub.service.SparkChatService;
import com.liuxy.campushub.utils.ApiAuthAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SparkChatServiceImpl implements SparkChatService {
    private static final Logger logger = LoggerFactory.getLogger(SparkChatServiceImpl.class);

    @Autowired
    private SparkConfig sparkConfig;

    @Override
    public SparkChatResponse chat(SparkChatRequest request) {
        try {
            // 生成鉴权参数
            long timestamp = System.currentTimeMillis() / 1000;
            String signature = ApiAuthAlgorithm.getSignature(
                sparkConfig.getAppId(),
                sparkConfig.getSecret(),
                timestamp
            );

            // 构建WebSocket URL - 使用知识库文档问答的URL
            String url = String.format(
                "wss://chatdoc.xfyun.cn/openapi/chat?appId=%s&timestamp=%d&signature=%s",
                sparkConfig.getAppId(),
                timestamp,
                signature
            );

            logger.debug("WebSocket URL: {}", url);

            // 创建WebSocket客户端
            StandardWebSocketClient client = new StandardWebSocketClient();
            
            // 创建CompletableFuture用于异步获取结果
            CompletableFuture<SparkChatResponse> future = new CompletableFuture<>();
            
            // 建立WebSocket连接
            WebSocketSession session = client.execute(
                new SparkWebSocketHandler(future),
                url
            ).get(10, TimeUnit.SECONDS);

            // 构建知识库问答请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fileIds", sparkConfig.getFileIds());
            
            // 构建消息历史
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加历史消息
            if (request.getHistory() != null) {
                for (SparkChatRequest.ChatMessage historyMessage : request.getHistory()) {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", historyMessage.getRole());
                    message.put("content", historyMessage.getContent());
                    messages.add(message);
                }
            }
            
            // 添加当前问题
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getQuestion());
            messages.add(userMessage);
            requestBody.put("messages", messages);

            // 添加知识库配置
            Map<String, Object> chatExtends = new HashMap<>();
            chatExtends.put("retrievalFilterPolicy", sparkConfig.getRetrievalFilterPolicy());
            chatExtends.put("temperature", sparkConfig.getTemperature());
            chatExtends.put("qaMode", sparkConfig.getQaMode());
            chatExtends.put("topN", sparkConfig.getTopN());
            requestBody.put("chatExtends", chatExtends);

            // 发送消息
            String message = JSON.toJSONString(requestBody);
            logger.debug("Sending request: {}", message);
            session.sendMessage(new TextMessage(message));

            // 等待响应
            SparkChatResponse response = future.get(30, TimeUnit.SECONDS);
            logger.debug("Received response: {}", JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            logger.error("Chat request failed", e);
            SparkChatResponse response = new SparkChatResponse();
            response.setCode(500);
            response.setMessage("请求失败: " + e.getMessage());
            return response;
        }
    }
} 