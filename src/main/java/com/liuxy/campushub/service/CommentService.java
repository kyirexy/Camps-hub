package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Comment;
import com.liuxy.campushub.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author liuxy
 * @since 2024-04-21
 */
public interface CommentService {
    
    /**
     * 创建评论
     *
     * @param comment 评论实体
     * @return 创建的评论ID
     */
    Long createComment(Comment comment);
    
    /**
     * 根据ID查询评论
     *
     * @param commentId 评论ID
     * @return 评论实体
     */
    Comment getCommentById(Long commentId);
    
    /**
     * 根据帖子ID查询评论列表（包含子评论）
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CommentVO> getCommentsByPostId(Long postId);
    
    /**
     * 根据用户ID查询评论列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    List<CommentVO> getCommentsByUserId(Long userId, int page, int pageSize);
    
    /**
     * 更新评论
     *
     * @param comment 评论实体
     * @return 是否更新成功
     */
    boolean updateComment(Comment comment);
    
    /**
     * 删除评论（软删除）
     *
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Long commentId);
    
    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return 是否点赞成功
     */
    boolean likeComment(Long commentId);
} 