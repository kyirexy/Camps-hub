package com.liuxy.campushub.entity;

import com.liuxy.campushub.enums.BountyStatusEnum;
import com.liuxy.campushub.enums.PostTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
public class Post {

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

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
     * 悬赏金额（当post_type=bounty时有效）
     */
    private BigDecimal bountyAmount;

    /**
     * 帖子类型: normal普通/bounty悬赏
     */
    @Enumerated(EnumType.STRING)
    private PostTypeEnum postType;

    /**
     * 悬赏状态: open待接单/processing进行中/completed已完成
     */
    @Enumerated(EnumType.STRING)
    @ManyToMany
@JoinTable(name = "post_topic",
    joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "topic_id"))
private List<Topic> topics;

private BountyStatusEnum bountyStatus;

    /**
     * 地理位置坐标（WGS84标准）
     */
    private String location;

    /**
     * 紧急程度（0-5级，0为普通）
     */
    private Integer emergencyLevel;

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

    /**
     * 附件列表
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

}