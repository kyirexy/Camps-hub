package com.liuxy.campushub.entity;

import com.liuxy.campushub.enums.BountyStatusEnum;
import com.liuxy.campushub.enums.PostTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帖子实体类（时间/空间优化：仅包含本表核心字段，关联关系通过DAO层处理）
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private Long userId; // 外键，关联student_user

    @Column(nullable = false)
    private Integer categoryId; // 外键，关联category

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostTypeEnum postType = PostTypeEnum.NORMAL; // 默认普通帖子

    @Column(precision = 8, scale = 2)
    private BigDecimal bountyAmount; // 悬赏金额（可为空）

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BountyStatusEnum bountyStatus; // 悬赏状态（可为空）

    @Column(nullable = false, columnDefinition = "tinyint unsigned default 0")
    private Integer emergencyLevel = 0; // 紧急程度，默认0

    @Column(nullable = false, columnDefinition = "int unsigned default 0")
    private Integer viewCount = 0;

    @Column(nullable = false, columnDefinition = "int unsigned default 0")
    private Integer likeCount = 0;

    @Column(nullable = false, columnDefinition = "int unsigned default 0")
    private Integer commentCount = 0;

    @Column(nullable = false, columnDefinition = "int unsigned default 0")
    private Integer shareCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatusEnum status = PostStatusEnum.PUBLISHED; // 默认已发布

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 创建时间自动填充

    @Column(name = "updated_at")
    private Date updatedAt;

    @Transient
    private String username;

    @Transient
    private String avatar;
    
    @Transient
    private Double hotness; // 热度值，不持久化到数据库

    // 多对多关联：帖子-话题（延迟加载，空间优化：初始化空列表避免NPE）
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_topic",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<Topic> topics = new ArrayList<>(); // 初始化空列表，避免空指针

    // 构造函数优化：仅初始化必要字段（可选，根据业务需求添加）
    public Post(Long userId, Integer categoryId, String title, String content) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }
}

/*
// 补充：状态枚举类（建议新增，提高类型安全）
public enum PostStatusEnum {
    DRAFT("draft"),
    PUBLISHED("published"),
    CLOSED("closed"),
    DELETED("deleted");

    private final String status;

    PostStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PostStatusEnum fromCode(String code) {
        for (PostStatusEnum status : values()) {
            if (status.getStatus().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid post status code: " + code);
    }
}*/
