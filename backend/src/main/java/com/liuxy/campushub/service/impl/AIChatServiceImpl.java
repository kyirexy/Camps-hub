package com.liuxy.campushub.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.liuxy.campushub.dto.ChatRequest;
import com.liuxy.campushub.dto.ChatResponse;
import com.liuxy.campushub.dto.ChatMessage;
import com.liuxy.campushub.service.AIChatService;
import com.liuxy.campushub.service.ChatContextService;
import com.liuxy.campushub.utils.SparkAIUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
public class AIChatServiceImpl implements AIChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIChatServiceImpl.class);
    private static final int MAX_HISTORY_LENGTH = 10;
    
    @Autowired
    private SparkAIUtil sparkAIUtil;
    
    @Autowired
    private ChatContextService chatContextService;
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            // 创建会话ID（如果不存在）
            String sessionId = request.getSessionId();
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
            }
            
            // 保存用户消息到上下文
            ChatMessage userMessage = ChatMessage.createUserMessage(request.getMessage());
            chatContextService.saveMessage(sessionId, userMessage);
            
            // 创建返回结果的 CompletableFuture
            CompletableFuture<String> responseFuture = new CompletableFuture<>();
            StringBuilder responseBuilder = new StringBuilder();
            
            // 创建 WebSocket 监听器
            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    JSONObject responseJson = JSONObject.parseObject(text);
                    JSONObject header = responseJson.getJSONObject("header");
                    if (header.getInteger("code") == 0) {
                        JSONObject payload = responseJson.getJSONObject("payload");
                        JSONObject choices = payload.getJSONObject("choices");
                        JSONArray textArray = choices.getJSONArray("text");
                        if (textArray != null && !textArray.isEmpty()) {
                            String content = textArray.getJSONObject(0).getString("content");
                            responseBuilder.append(content);
                        }
                        
                        // 如果是最后一条消息，完成 Future
                        if (choices.getInteger("status") == 2) {
                            String finalResponse = responseBuilder.toString();
                            // 保存助手回复到上下文
                            ChatMessage assistantMessage = ChatMessage.createAssistantMessage(finalResponse);
                            chatContextService.saveMessage(sessionId, assistantMessage);
                            responseFuture.complete(finalResponse);
                            webSocket.close(1000, "正常关闭");
                        }
                    } else {
                        responseFuture.completeExceptionally(
                            new RuntimeException("请求失败：" + header.getString("message"))
                        );
                        webSocket.close(1000, "错误关闭");
                    }
                }
                
                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    responseFuture.completeExceptionally(t);
                }
            };
            
            // 创建 WebSocket 连接
            WebSocket webSocket = sparkAIUtil.createWebSocket(listener);
            
            // 获取历史消息
            List<ChatMessage> history = chatContextService.getRecentHistory(sessionId, MAX_HISTORY_LENGTH);
            
            // 构建请求消息
            JSONObject requestJson = new JSONObject();
            
            // 添加 header
            JSONObject header = new JSONObject();
            header.put("app_id", sparkAIUtil.getAppId());
            header.put("uid", sessionId);
            requestJson.put("header", header);
            
            // 添加 parameter
            JSONObject parameter = new JSONObject();
            JSONObject chat = new JSONObject();
            chat.put("domain", "4.0Ultra");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 1024);
            chat.put("top_k", 4);
            chat.put("random_threshold", 0.5);
            chat.put("auditing", "default");
            parameter.put("chat", chat);
            requestJson.put("parameter", parameter);
            
            // 添加 payload
            JSONObject payload = new JSONObject();
            JSONObject message = new JSONObject();
            
            // 构建历史消息数组
            JSONArray textArray = new JSONArray();
            for (ChatMessage msg : history) {
                JSONObject textObj = new JSONObject();
                textObj.put("role", msg.getRole());
                textObj.put("content", msg.getContent());
                textArray.add(textObj);
            }
            
            message.put("text", textArray);
            payload.put("message", message);
            requestJson.put("payload", payload);
            
            // 发送消息
            String requestString = requestJson.toJSONString();
            logger.debug("发送请求: {}", requestString);
            webSocket.send(requestString);
            
            // 等待响应，设置超时时间为30秒
            String response = responseFuture.get(30, TimeUnit.SECONDS);
            
            return ChatResponse.success(response, sessionId);
            
        } catch (Exception e) {
            logger.error("AI聊天服务异常", e);
            return ChatResponse.error("AI服务暂时不可用: " + e.getMessage());
        }
    }
} 