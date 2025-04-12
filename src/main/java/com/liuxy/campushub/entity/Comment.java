package com.liuxy.campushub.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Comment {
    /**
     * 评论ID
     */
    private Integer commentId;

    /**
     * 帖子ID
     */
    private Integer postId;

    /**
     * 评论者ID
     */
    private Long userId;

    /**
     * 父评论ID（用于回复功能）
     */
    private Integer parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态（0-待审核，1-已通过，2-已拒绝）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 