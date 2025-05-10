package com.liuxy.campushub.model;

import com.liuxy.campushub.entity.Post;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 热点帖子热度计算模型
 * 
 * @author liuxy
 * @since 2024-04-27
 */
@Data
@Slf4j
public class HotPostModel {
    
    /**
     * 热度计算权重常量
     */
    private static final double VIEW_WEIGHT = 0.2;    // 浏览量权重
    private static final double LIKE_WEIGHT = 0.3;    // 点赞量权重
    private static final double COMMENT_WEIGHT = 0.25; // 评论量权重
    private static final double SHARE_WEIGHT = 0.1;   // 分享量权重
    private static final double COLLECT_WEIGHT = 0.15; // 收藏量权重
    
    /**
     * 时间衰减系数
     */
    private static final double TIME_DECAY_FACTOR = 0.2;
    
    /**
     * 突发系数阈值
     */
    private static final int BURST_THRESHOLD = 100;
    
    /**
     * 突发系数倍数
     */
    private static final double BURST_MULTIPLIER = 1.5;
    
    /**
     * 计算帖子热度值
     * 
     * @param post 帖子实体
     * @return 热度值
     */
    public static double calculateHotness(Post post) {
        // 基础指标计算
        double baseScore = 
            VIEW_WEIGHT * post.getViewCount() +
            LIKE_WEIGHT * post.getLikeCount() +
            COMMENT_WEIGHT * post.getCommentCount() +
            SHARE_WEIGHT * post.getShareCount() +
            COLLECT_WEIGHT * post.getLikeCount(); // 暂时用点赞数代替收藏数
        
        // 时间衰减计算
        LocalDateTime now = LocalDateTime.now();
        long hoursExisted = ChronoUnit.HOURS.between(post.getCreatedAt(), now);
        double decayFactor = Math.exp(-TIME_DECAY_FACTOR * hoursExisted / 24.0); // 按天衰减
        
        // 突发检测（如果1小时内评论量超过阈值，触发突发系数）
        double burstBoost = 1.0;
        if (post.getCommentCount() > BURST_THRESHOLD) {
            burstBoost = BURST_MULTIPLIER;
            log.debug("帖子[{}]触发突发系数，评论量: {}", post.getPostId(), post.getCommentCount());
        }
        
        // 最终热度计算
        double finalScore = baseScore * decayFactor * burstBoost;
        
        log.debug("帖子[{}]热度计算: 基础分={}, 衰减系数={}, 突发系数={}, 最终热度={}", 
                post.getPostId(), baseScore, decayFactor, burstBoost, finalScore);
        
        return finalScore;
    }
    
    /**
     * 判断帖子是否为热点
     * 
     * @param post 帖子实体
     * @param threshold 热点阈值
     * @return 是否为热点
     */
    public static boolean isHotPost(Post post, double threshold) {
        double hotness = calculateHotness(post);
        return hotness >= threshold;
    }
} 