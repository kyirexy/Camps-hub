package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Topic;
import com.liuxy.campushub.mapper.TopicMapper;
import com.liuxy.campushub.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.liuxy.campushub.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 话题服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class TopicServiceImpl implements TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Autowired
    private TopicMapper topicMapper;

    @Override
    @Transactional
    public Integer createTopic(Topic topic) {
        topicMapper.insert(topic);
        return topic.getTopicId();
    }

    @Override
    public Topic getTopicById(Integer topicId) {
        return topicMapper.selectById(topicId);
    }

    @Override
    public Topic getTopicByName(String topicName) {
        return topicMapper.selectByName(topicName);
    }

    @Override
    public List<Topic> getHotTopics(int limit) {
        return topicMapper.selectHotTopics(limit);
    }

    @Override
    @Transactional
    public boolean incrementUsageCount(Integer topicId) {
        return topicMapper.incrementUsageCount(topicId) > 0;
    }

    @Override
    @Transactional
    public boolean linkPostTopic(Long postId, Integer topicId) {
        return topicMapper.insertPostTopic(postId, topicId) > 0;
    }

    @Override
    @Transactional
    public boolean batchLinkPostTopic(Long postId, List<Integer> topicIds) {
        for (Integer topicId : topicIds) {
            if (!linkPostTopic(postId, topicId)) {
                return false;
            }
            incrementUsageCount(topicId);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean unlinkPostTopic(Long postId, Integer topicId) {
        return topicMapper.unlinkPostTopic(postId, topicId) > 0;
    }

    @Override
    public List<Topic> getTopicsByPostId(Long postId) {
        return topicMapper.selectByPostId(postId);
    }

    @Override
    public List<Topic> searchTopics(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return topicMapper.search(keyword, offset, pageSize);
    }

    @Override
    public List<Topic> extractTopicsFromContent(String content) {
        List<Topic> topics = new ArrayList<>();
        Pattern pattern = Pattern.compile("#([^#]+)#");
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String topicName = matcher.group(1).trim();
            Topic topic = getTopicByName(topicName);
            if (topic != null) {
                topics.add(topic);
            }
        }
        return topics;
    }

    @Override
    @Transactional
    public Topic getOrCreateTopic(String topicName, Long creatorId) {
        Topic topic = getTopicByName(topicName);
        if (topic == null) {
            topic = new Topic();
            topic.setTopicName(topicName);
            topic.setCreatorId(creatorId);
            topic.setUsageCount(0);
            createTopic(topic);
        }
        return topic;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlinkAllPostTopics(Long postId) {
        try {
            logger.info("开始解除帖子与所有话题的关联，postId: {}", postId);
            
            // 获取帖子关联的所有话题ID
            List<Integer> topicIds = topicMapper.getTopicIdsByPostId(postId);
            if (topicIds.isEmpty()) {
                logger.info("帖子没有关联任何话题，postId: {}", postId);
                return true;
            }
            
            // 批量解除关联
            for (Integer topicId : topicIds) {
                topicMapper.unlinkPostTopic(postId, topicId);
            }
            
            logger.info("成功解除帖子与所有话题的关联，postId: {}, 解除关联的话题数: {}", postId, topicIds.size());
            return true;
        } catch (Exception e) {
            logger.error("解除帖子与所有话题关联失败，postId: {}", postId, e);
            throw new BusinessException("解除帖子与所有话题关联失败: " + e.getMessage());
        }
    }
} 