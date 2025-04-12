package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Topic;
import com.liuxy.campushub.mapper.TopicMapper;
import com.liuxy.campushub.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return topicMapper.deletePostTopic(postId, topicId) > 0;
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
} 