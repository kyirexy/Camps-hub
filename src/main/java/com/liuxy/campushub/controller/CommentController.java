package com.liuxy.campushub.controller;

import com.liuxy.campushub.common.Result;
import com.liuxy.campushub.entity.Comment;
import com.liuxy.campushub.service.CommentService;
import com.liuxy.campushub.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author liuxy
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    /**
     * 创建评论
     *
     * @param comment 评论信息
     * @return 评论ID
     */
    @PostMapping
    public Result<Long> createComment(@RequestBody Comment comment) {
        logger.info("创建评论请求，comment: {}", comment);
        try {
            // 从安全上下文获取当前用户ID
            Long userId = com.liuxy.campushub.utils.SecurityUtil.getCurrentUserId();
            logger.info("从安全上下文获取到当前用户ID: {}", userId);
            comment.setUserId(userId);
        } catch (Exception e) {
            logger.error("获取当前用户ID失败", e);
            return Result.error("无法获取用户ID");
        }
        Long commentId = commentService.createComment(comment);
        return Result.success(commentId);
    }

    /**
     * 获取评论详情
     *
     * @param commentId 评论ID
     * @return 评论详情
     */
    @GetMapping("/{commentId}")
    public Result<Comment> getComment(@PathVariable Long commentId) {
        logger.info("获取评论详情请求，commentId: {}", commentId);
        Comment comment = commentService.getCommentById(commentId);
        return Result.success(comment);
    }

    /**
     * 获取帖子评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    @GetMapping("/post/{postId}")
    public Result<List<CommentVO>> getCommentsByPost(@PathVariable Long postId) {
        logger.info("获取帖子评论列表请求，postId: {}", postId);
        List<CommentVO> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 获取用户评论列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<CommentVO>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        logger.info("获取用户评论列表请求，userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
        List<CommentVO> comments = commentService.getCommentsByUserId(userId, page, pageSize);
        return Result.success(comments);
    }

    /**
     * 更新评论
     *
     * @param commentId 评论ID
     * @param comment 评论信息
     * @return 更新结果
     */
    @PutMapping("/{commentId}")
    public Result<Boolean> updateComment(
            @PathVariable Long commentId,
            @RequestBody Comment comment) {
        logger.info("更新评论请求，commentId: {}, comment: {}", commentId, comment);
        comment.setCommentId(commentId);
        boolean success = commentService.updateComment(comment);
        return Result.success(success);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        logger.info("删除评论请求，commentId: {}", commentId);
        boolean success = commentService.deleteComment(commentId);
        return Result.success(success);
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return 点赞结果
     */
    @PostMapping("/{commentId}/like")
    public Result<Boolean> likeComment(@PathVariable Long commentId) {
        logger.info("点赞评论请求，commentId: {}", commentId);
        boolean success = commentService.likeComment(commentId);
        return Result.success(success);
    }
} 