package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.vo.PostVO;
import com.liuxy.campushub.vo.ScrollResult;
import com.liuxy.campushub.vo.PostDetailResponseVO;
import com.liuxy.campushub.vo.HotPostVO;
import java.util.Date;
import java.util.List;

/**
 * 帖子服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface PostService {
    
    /**
     * 创建新帖子
     *
     * @param post 帖子实体
     * @param base64Images base64编码的图片列表
     * @return 创建的帖子ID
     */
    Long createPost(Post post, List<String> base64Images);
    
    /**
     * 更新帖子信息
     *
     * @param post 帖子实体
     * @param base64Images base64编码的图片列表
     * @return 是否更新成功
     */
    boolean updatePost(Post post, List<String> base64Images);
    
    /**
     * 根据ID查询帖子
     *
     * @param postId 帖子ID
     * @return 帖子实体
     */
    Post getPostById(Long postId);
    
    /**
     * 获取帖子列表（滚动分页）
     *
     * @param lastTime 最后一条记录的时间戳（首次传空）
     * @param pageSize 每页数量
     * @return 滚动分页结果
     */
    ScrollResult<PostVO> getPostList(Date lastTime, int pageSize);
    
    /**
     * 根据分类ID查询帖子（滚动分页）
     *
     * @param categoryId 分类ID
     * @param lastTime 最后一条记录的时间戳（首次传空）
     * @param pageSize 每页数量
     * @return 滚动分页结果
     */
    ScrollResult<PostVO> getPostsByCategory(Integer categoryId, Date lastTime, int pageSize);
    
    /**
     * 根据用户ID查询帖子（滚动分页）
     *
     * @param userId 用户ID
     * @param lastTime 最后一条记录的时间戳（首次传空）
     * @param pageSize 每页数量
     * @return 滚动分页结果
     */
    ScrollResult<PostVO> getPostsByUser(Long userId, Date lastTime, int pageSize);
    
    /**
     * 更新帖子状态
     *
     * @param postId 帖子ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updatePostStatus(Long postId, String status);
    
    /**
     * 增加帖子浏览量
     *
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean incrementViewCount(Long postId);
    
    /**
     * 增加帖子点赞数
     *
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean incrementLikeCount(Long postId);
    
    /**
     * 增加帖子评论数
     *
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean incrementCommentCount(Long postId);
    
    /**
     * 增加帖子分享数
     *
     * @param postId 帖子ID
     * @return 是否更新成功
     */
    boolean incrementShareCount(Long postId);
    
    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @return 是否删除成功
     */
    boolean deletePost(Long postId);
    
    /**
     * 瀑布流加载帖子列表
     *
     * @param lastTime 最后一条记录的时间戳（首次加载传null）
     * @param limit 获取条数
     * @return 滚动分页结果
     */
    ScrollResult<PostVO> getPostsWaterfall(Date lastTime, int limit);
    
    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情VO
     */
    PostDetailResponseVO getPostDetail(Long postId);

    /**
     * 根据用户ID分页查询帖子列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 滚动分页结果
     */
    ScrollResult<PostVO> getPostsByUserId(Long userId, int page, int pageSize);

    /**
     * 获取热点帖子列表
     *
     * @param limit 获取条数，默认10条
     * @return 热点帖子列表
     */
    List<HotPostVO> getHotPosts(int limit);
    
    /**
     * 获取热点帖子列表（带分页）
     *
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 热点帖子分页结果
     */
    ScrollResult<HotPostVO> getHotPostsWithPagination(int page, int pageSize);
    
    /**
     * 获取突发热点帖子列表
     *
     * @param limit 获取条数，默认5条
     * @return 突发热点帖子列表
     */
    List<HotPostVO> getBurstHotPosts(int limit);

    /**
     * 获取5天内按热度排序的热点帖子
     * @param limit 获取条数
     * @return 热点帖子列表
     */
    List<Post> getLatestPosts(int limit);
} 