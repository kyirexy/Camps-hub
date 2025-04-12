package com.liuxy.campushub.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 帖子实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Post {
    /**
     * 帖子唯一ID
     */
    private Long postId;

    /**
     * 发帖人ID
     */
    private Long userId;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 正文内容（富文本）
     */
    private String content;

    /**
     * 帖子类型: normal普通/bounty悬赏/lost失物/trade交易
     */
    private String postType;

    /**
     * 悬赏金额
     */
    private BigDecimal bountyAmount;

    /**
     * 悬赏状态: open待接单/processing进行中/completed已完成
     */
    private String bountyStatus;

    /**
     * 紧急程度（0-5级，0为普通）
     */
    private Integer emergencyLevel;

    /**
     * 地理位置坐标（WGS84标准）
     */
    private String location;

    /**
     * 浏览数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 状态: draft草稿/published已发布/closed已关闭/deleted已删除
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
} 