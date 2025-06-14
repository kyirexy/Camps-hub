package com.liuxy.campushub.handler;

import com.liuxy.campushub.dto.SparkChatResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.CompletableFuture;

public class SparkWebSocketHandler extends TextWebSocketHandler {
    private final CompletableFuture<SparkChatResponse> future;
    private final StringBuilder contentBuilder = new StringBuilder();

    public SparkWebSocketHandler(CompletableFuture<SparkChatResponse> future) {
        this.future = future;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            String text = ((TextMessage) message).getPayload();
            SparkChatResponse response = JSON.parseObject(text, SparkChatResponse.class);
            
            // 累积内容
            if (response.getContent() != null) {
                contentBuilder.append(response.getContent());
            }
            
            // 如果是最后一个消息，完成future
            if (response.getStatus() == 2) {
                response.setContent(contentBuilder.toString());
                future.complete(response);
                session.close();
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        SparkChatResponse response = new SparkChatResponse();
        response.setCode(500);
        response.setMessage("WebSocket传输错误: " + exception.getMessage());
        future.complete(response);
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (!future.isDone()) {
            SparkChatResponse response = new SparkChatResponse();
            response.setCode(500);
            response.setMessage("WebSocket连接已关闭: " + status.getReason());
            future.complete(response);
        }
    }
} 