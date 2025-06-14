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

@Service
public class SparkChatServiceImpl implements SparkChatService {

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
            requestBody.put("fileIds", new String[]{sparkConfig.getRepoId()});
            
            // 构建消息历史
            List<Map<String, String>> messages = new ArrayList<>();
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
            requestBody.put("chatExtends", chatExtends);

            // 发送消息
            String message = JSON.toJSONString(requestBody);
            session.sendMessage(new TextMessage(message));

            // 等待响应
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            SparkChatResponse response = new SparkChatResponse();
            response.setCode(500);
            response.setMessage("请求失败: " + e.getMessage());
            return response;
        }
    }
} 