package com.liuxy.campushub.entity;

import com.liuxy.campushub.enums.CommentStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 评论实体类
 *
 * @author liuxy
 * @since 2024-04-21
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_id")
    private Long parentId = 0L; // 默认值为0，表示顶级评论

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CommentStatusEnum status = CommentStatusEnum.NORMAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt = new Date();

    // 非数据库字段，用于关联查询
    @Transient
    private String username;

    @Transient
    private String avatar;

    // 构造函数
    public Comment(Long postId, Long userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    // 带父评论ID的构造函数
    public Comment(Long postId, Long userId, Long parentId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.parentId = parentId;
        this.content = content;
    }
} 