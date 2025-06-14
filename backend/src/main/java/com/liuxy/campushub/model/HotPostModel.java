package com.liuxy.campushub.model;

import com.liuxy.campushub.entity.Post;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 热点帖子计算模型
 */
@Slf4j
public class HotPostModel {
    
    // 热度计算权重
    private static final double VIEW_WEIGHT = 0.2;    // 浏览量权重
    private static final double LIKE_WEIGHT = 0.3;    // 点赞量权重
    private static final double COMMENT_WEIGHT = 0.25; // 评论量权重
    private static final double SHARE_WEIGHT = 0.1;   // 分享量权重
    private static final double TIME_DECAY_FACTOR = 0.2; // 时间衰减因子
    
    // 突发阈值
    private static final int BURST_COMMENT_THRESHOLD = 100; // 突发评论数阈值
    private static final int BURST_TIME_THRESHOLD = 24; // 突发时间阈值（小时）
    
    /**
     * 计算帖子热度值
     * 
     * @param post 帖子实体
     * @return 热度值
     */
    public static double calculateHotness(Post post) {
        try {
            // 获取时间衰减因子
            long hoursSinceCreation = ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now());
            double timeDecay = Math.pow(TIME_DECAY_FACTOR, hoursSinceCreation / 24.0); // 按天衰减
            
            // 计算基础热度值
            double baseHotness = post.getViewCount() * VIEW_WEIGHT +
                               post.getLikeCount() * LIKE_WEIGHT +
                               post.getCommentCount() * COMMENT_WEIGHT +
                               post.getShareCount() * SHARE_WEIGHT;
            
            // 突发系数（如果24小时内评论数超过阈值，给予额外热度）
            double burstMultiplier = 1.0;
            if (isBurstHot(post)) {
                burstMultiplier = 1.5;
                log.debug("帖子[{}]触发突发系数，评论数: {}", post.getPostId(), post.getCommentCount());
            }
            
            // 最终热度计算
            double finalHotness = baseHotness * timeDecay * burstMultiplier;
            
            log.debug("帖子[{}]热度计算: 基础分={}, 衰减系数={}, 突发系数={}, 最终热度={}", 
                    post.getPostId(), baseHotness, timeDecay, burstMultiplier, finalHotness);
            
            return finalHotness;
        } catch (Exception e) {
            log.error("计算帖子热度失败: postId=" + post.getPostId(), e);
            return 0.0;
        }
    }
    
    /**
     * 判断是否为突发热点
     * 
     * @param post 帖子实体
     * @return 是否为突发热点
     */
    public static boolean isBurstHot(Post post) {
        return post.getCommentCount() > BURST_COMMENT_THRESHOLD && 
               ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now()) <= BURST_TIME_THRESHOLD;
    }
    
    /**
     * 判断是否为新发布
     * 
     * @param post 帖子实体
     * @return 是否为新发布
     */
    public static boolean isNewPost(Post post) {
        return ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now()) <= BURST_TIME_THRESHOLD;
    }
} 