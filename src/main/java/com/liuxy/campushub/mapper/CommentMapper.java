package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Comment;
import com.liuxy.campushub.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 评论Mapper接口
 *
 * @author liuxy
 * @since 2024-04-21
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 插入评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int insert(Comment comment);
    
    /**
     * 根据ID查询评论
     *
     * @param commentId 评论ID
     * @return 评论实体
     */
    Comment selectById(Long commentId);
    
    /**
     * 根据帖子ID查询评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CommentVO> selectByPostId(Long postId);
    
    /**
     * 根据父评论ID查询子评论
     *
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<CommentVO> selectByParentId(Long parentId);
    
    /**
     * 更新评论
     *
     * @param comment 评论实体
     * @return 影响行数
     */
    int updateById(Comment comment);
    
    /**
     * 删除评论（软删除）
     *
     * @param commentId 评论ID
     * @return 影响行数
     */
    int deleteById(Long commentId);
    
    /**
     * 增加评论点赞数
     *
     * @param commentId 评论ID
     * @return 影响行数
     */
    int incrementLikeCount(Long commentId);
    
    /**
     * 根据用户ID查询评论列表
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 评论列表
     */
    List<CommentVO> selectByUserId(@Param("userId") Long userId, 
                                  @Param("offset") int offset, 
                                  @Param("pageSize") int pageSize);
    
    /**
     * 分页查询评论列表
     *
     * @param params 查询参数
     * @return 评论列表
     */
    List<CommentVO> selectForPage(Map<String, Object> params);
} 