package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 互动实体类（点赞、收藏等）
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Interaction {
    /**
     * 互动ID
     */
    private Integer interactionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 互动类型（1-点赞，2-收藏，3-分享）
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 