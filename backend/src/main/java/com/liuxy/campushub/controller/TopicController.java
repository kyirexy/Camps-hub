package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.Topic;
import com.liuxy.campushub.service.TopicService;
import com.liuxy.campushub.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 话题控制器
 *
 * @author liuxy
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/api/topics")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    /**
     * 创建新话题
     *
     * @param topic 话题信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Integer> createTopic(@RequestBody Topic topic) {
        try {
            Integer topicId = topicService.createTopic(topic);
            return Result.success(topicId);
        } catch (Exception e) {
            return Result.error("创建话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取话题详情
     *
     * @param topicId 话题ID
     * @return 话题详情
     */
    @GetMapping("/{topicId}")
    public Result<Topic> getTopic(@PathVariable Integer topicId) {
        try {
            Topic topic = topicService.getTopicById(topicId);
            if (topic == null) {
                return Result.error("话题不存在");
            }
            return Result.success(topic);
        } catch (Exception e) {
            return Result.error("获取话题详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据名称获取话题
     *
     * @param topicName 话题名称
     * @return 话题信息
     */
    @GetMapping("/name/{topicName}")
    public Result<Topic> getTopicByName(@PathVariable String topicName) {
        try {
            Topic topic = topicService.getTopicByName(topicName);
            if (topic == null) {
                return Result.error("话题不存在");
            }
            return Result.success(topic);
        } catch (Exception e) {
            return Result.error("获取话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门话题列表
     *
     * @param limit 限制数量
     * @return 话题列表
     */
    @GetMapping("/hot")
    public Result<List<Topic>> getHotTopics(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Topic> topics = topicService.getHotTopics(limit);
            return Result.success(topics);
        } catch (Exception e) {
            return Result.error("获取热门话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 增加话题使用次数
     *
     * @param topicId 话题ID
     * @return 操作结果
     */
    @PostMapping("/{topicId}/increment")
    public Result<Boolean> incrementUsageCount(@PathVariable Integer topicId) {
        try {
            boolean success = topicService.incrementUsageCount(topicId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("更新话题使用次数失败: " + e.getMessage());
        }
    }
    
    /**
     * 关联帖子和话题
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 操作结果
     */
    @PostMapping("/{topicId}/posts/{postId}")
    public Result<Boolean> linkPostTopic(@PathVariable Long postId, @PathVariable Integer topicId) {
        try {
            boolean success = topicService.linkPostTopic(postId, topicId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("关联帖子和话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量关联帖子和话题
     *
     * @param postId 帖子ID
     * @param topicIds 话题ID列表
     * @return 操作结果
     */
    @PostMapping("/posts/{postId}/batch")
    public Result<Boolean> batchLinkPostTopic(@PathVariable Long postId, @RequestBody List<Integer> topicIds) {
        try {
            boolean success = topicService.batchLinkPostTopic(postId, topicIds);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("批量关联帖子和话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 解除帖子和话题的关联
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 操作结果
     */
    @DeleteMapping("/{topicId}/posts/{postId}")
    public Result<Boolean> unlinkPostTopic(@PathVariable Long postId, @PathVariable Integer topicId) {
        try {
            boolean success = topicService.unlinkPostTopic(postId, topicId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("解除帖子和话题关联失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取帖子关联的所有话题
     *
     * @param postId 帖子ID
     * @return 话题列表
     */
    @GetMapping("/posts/{postId}")
    public Result<List<Topic>> getTopicsByPostId(@PathVariable Long postId) {
        try {
            List<Topic> topics = topicService.getTopicsByPostId(postId);
            return Result.success(topics);
        } catch (Exception e) {
            return Result.error("获取帖子话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索话题
     *
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 话题列表
     */
    @GetMapping("/search")
    public Result<List<Topic>> searchTopics(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Topic> topics = topicService.searchTopics(keyword, pageNum, pageSize);
            return Result.success(topics);
        } catch (Exception e) {
            return Result.error("搜索话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 从文本内容中提取话题
     *
     * @param content 文本内容
     * @return 话题列表
     */
    @PostMapping("/extract")
    public Result<List<Topic>> extractTopicsFromContent(@RequestBody String content) {
        try {
            List<Topic> topics = topicService.extractTopicsFromContent(content);
            return Result.success(topics);
        } catch (Exception e) {
            return Result.error("提取话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取或创建话题
     *
     * @param topicName 话题名称
     * @param creatorId 创建者ID
     * @return 话题信息
     */
    @PostMapping("/get-or-create")
    public Result<Topic> getOrCreateTopic(@RequestParam String topicName, @RequestParam Long creatorId) {
        try {
            Topic topic = topicService.getOrCreateTopic(topicName, creatorId);
            return Result.success(topic);
        } catch (Exception e) {
            return Result.error("获取或创建话题失败: " + e.getMessage());
        }
    }
} 