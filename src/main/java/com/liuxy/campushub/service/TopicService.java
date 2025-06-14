package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Topic;
import java.util.List;

/**
 * 话题服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface TopicService {
    
    /**
     * 创建新话题
     *
     * @param topic 话题实体
     * @return 创建的话题ID
     */
    Integer createTopic(Topic topic);
    
    /**
     * 根据ID查询话题
     *
     * @param topicId 话题ID
     * @return 话题实体
     */
    Topic getTopicById(Integer topicId);
    
    /**
     * 根据名称查询话题
     *
     * @param topicName 话题名称
     * @return 话题实体
     */
    Topic getTopicByName(String topicName);
    
    /**
     * 查询热门话题
     *
     * @param limit 限制数量
     * @return 话题列表
     */
    List<Topic> getHotTopics(int limit);
    
    /**
     * 增加话题使用次数
     *
     * @param topicId 话题ID
     * @return 是否更新成功
     */
    boolean incrementUsageCount(Integer topicId);
    
    /**
     * 关联帖子与话题
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 是否关联成功
     */
    boolean linkPostTopic(Long postId, Integer topicId);
    
    /**
     * 批量关联帖子与话题
     *
     * @param postId 帖子ID
     * @param topicIds 话题ID列表
     * @return 是否关联成功
     */
    boolean batchLinkPostTopic(Long postId, List<Integer> topicIds);
    
    /**
     * 解除帖子与话题的关联
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 是否解除成功
     */
    boolean unlinkPostTopic(Long postId, Integer topicId);
    
    /**
     * 查询帖子关联的所有话题
     *
     * @param postId 帖子ID
     * @return 话题列表
     */
    List<Topic> getTopicsByPostId(Long postId);
    
    /**
     * 搜索话题
     *
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 话题列表
     */
    List<Topic> searchTopics(String keyword, int pageNum, int pageSize);
    
    /**
     * 从文本中提取话题
     *
     * @param content 文本内容
     * @return 话题列表
     */
    List<Topic> extractTopicsFromContent(String content);
    
    /**
     * 获取或创建话题
     *
     * @param topicName 话题名称
     * @param creatorId 创建者ID
     * @return 话题实体
     */
    Topic getOrCreateTopic(String topicName, Long creatorId);
    
    /**
     * 解除帖子与所有话题的关联
     *
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean unlinkAllPostTopics(Long postId);
} 