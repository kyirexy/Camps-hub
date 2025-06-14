package com.liuxy.campushub.vo;

import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.entity.Topic;
import com.liuxy.campushub.enums.PostTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostDetailResponseVO {
    private Long postId;
    private Long userId;
    private Integer categoryId;
    private String title;
    private String content;
    private PostTypeEnum postType;
    private BigDecimal bountyAmount;
    private String bountyStatus; // Using String for simplicity in VO
    private Integer emergencyLevel;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private String status; // Using String for simplicity in VO
    private LocalDateTime createdAt;
    private Date updatedAt;
    
    // 用户相关信息
    private String username;
    private String avatar; // 完整的头像URL

    // 话题列表
    private List<TopicVO> topics;

    // 可以根据需要添加其他字段，例如附件、失物招领信息等

    // 内部类或单独文件定义 TopicVO，用于只包含需要展示的话题信息
    @Data
    public static class TopicVO {
        private Integer topicId;
        private String topicName;
        // 可以根据需要添加话题创建者信息等
         private Long creatorId;
         private String creatorUsername;
         private String creatorAvatarUrl; // 话题创建者头像URL
    }

    // 静态方法用于从 Post 和 List<Topic> 构建 PostDetailResponseVO
    public static PostDetailResponseVO fromEntities(Post post, List<Topic> topics, String imageBaseUrl, String avatarPath) {
        PostDetailResponseVO vo = new PostDetailResponseVO();
        
        // 复制 Post 实体字段
        vo.setPostId(post.getPostId());
        vo.setUserId(post.getUserId());
        vo.setCategoryId(post.getCategoryId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setPostType(post.getPostType());
        vo.setBountyAmount(post.getBountyAmount());
        vo.setBountyStatus(post.getBountyStatus() != null ? post.getBountyStatus().getCode() : null); // Convert enum to string
        vo.setEmergencyLevel(post.getEmergencyLevel());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setShareCount(post.getShareCount());
        vo.setStatus(post.getStatus() != null ? post.getStatus().getCode() : null); // Convert enum to string
        vo.setCreatedAt(post.getCreatedAt());
        vo.setUpdatedAt(post.getUpdatedAt());
        
        // 设置用户相关信息，拼接完整的头像URL
        vo.setUsername(post.getUsername());
        if (post.getAvatar() != null) {
            vo.setAvatar(imageBaseUrl + avatarPath + "/" + post.getAvatar());
        }
        
        // 处理话题列表
        if (topics != null) {
            vo.setTopics(topics.stream().map(topic -> {
                TopicVO topicVO = new TopicVO();
                topicVO.setTopicId(topic.getTopicId());
                topicVO.setTopicName(topic.getTopicName());
                // 处理话题创建者信息，拼接头像URL (assuming Topic entity has creator info fields)
                topicVO.setCreatorId(topic.getCreatorId());
                // You might need to fetch creator username and avatar filename for topic creators
                // For now, let's assume topic entity has username and avatarUrl fields
                // If not, you might need TopicService to get creator details by creatorId
                topicVO.setCreatorUsername(topic.getUsername()); // Assuming Topic entity has this
                if (topic.getAvatarUrl() != null) {
                    topicVO.setCreatorAvatarUrl(imageBaseUrl + avatarPath + "/" + topic.getAvatarUrl());
                }
                return topicVO;
            }).collect(Collectors.toList()));
        }
        
        return vo;
    }

} 