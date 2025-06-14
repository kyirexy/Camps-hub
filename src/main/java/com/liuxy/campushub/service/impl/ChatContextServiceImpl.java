package com.liuxy.campushub.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxy.campushub.dto.ChatMessage;
import com.liuxy.campushub.service.ChatContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatContextServiceImpl implements ChatContextService {

    private static final String CHAT_CONTEXT_KEY_PREFIX = "chat:context:";
    private static final Duration CONTEXT_EXPIRATION = Duration.ofHours(24);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveMessage(String sessionId, ChatMessage message) {
        String key = CHAT_CONTEXT_KEY_PREFIX + sessionId;
        String messageJson = JSON.toJSONString(message);
        
        // 使用消息ID作为field，消息内容作为value
        redisTemplate.opsForHash().put(key, message.getMessageId(), messageJson);
        
        // 设置过期时间
        redisTemplate.expire(key, CONTEXT_EXPIRATION);
    }

    @Override
    public List<ChatMessage> getHistory(String sessionId) {
        String key = CHAT_CONTEXT_KEY_PREFIX + sessionId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        
        return entries.values().stream()
                .map(value -> JSON.parseObject(value.toString(), ChatMessage.class))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public void clearHistory(String sessionId) {
        String key = CHAT_CONTEXT_KEY_PREFIX + sessionId;
        redisTemplate.delete(key);
    }

    @Override
    public List<ChatMessage> getRecentHistory(String sessionId, int limit) {
        List<ChatMessage> allHistory = getHistory(sessionId);
        if (allHistory.size() <= limit) {
            return allHistory;
        }
        return allHistory.subList(allHistory.size() - limit, allHistory.size());
    }
} 