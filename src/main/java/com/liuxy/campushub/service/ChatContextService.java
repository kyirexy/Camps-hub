package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.ChatMessage;
import java.util.List;

public interface ChatContextService {
    /**
     * 保存对话消息到上下文
     *
     * @param sessionId 会话ID
     * @param message 消息内容
     */
    void saveMessage(String sessionId, ChatMessage message);

    /**
     * 获取会话历史消息
     *
     * @param sessionId 会话ID
     * @return 历史消息列表
     */
    List<ChatMessage> getHistory(String sessionId);

    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     */
    void clearHistory(String sessionId);

    /**
     * 获取最近的N条历史消息
     *
     * @param sessionId 会话ID
     * @param limit 消息数量限制
     * @return 历史消息列表
     */
    List<ChatMessage> getRecentHistory(String sessionId, int limit);
} 