package com.liuxy.campushub.vo;

import com.liuxy.campushub.entity.Comment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 评论视图对象
 *
 * @author liuxy
 * @since 2024-04-21
 */
@Data
public class CommentVO {
    
    private Long commentId;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Date createdAt;
    private String username;
    private String avatar;
    
    // 子评论列表
    private List<CommentVO> children;
    
    // 构造函数
    public CommentVO() {
    }
    
    // 从Comment实体转换为CommentVO
    public CommentVO(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPostId();
        this.userId = comment.getUserId();
        this.parentId = comment.getParentId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.createdAt = comment.getCreatedAt();
        this.username = comment.getUsername();
        this.avatar = comment.getAvatar();
    }
} 