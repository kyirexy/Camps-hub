package com.liuxy.campushub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.entity.PostStatusEnum;
import com.liuxy.campushub.vo.PostVO;
import org.apache.ibatis.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 帖子数据访问层
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    void insertPostTopics(@Param("postId") Long postId, @Param("topicIds") List<Integer> topicIds);
    void deletePostTopicsByPostId(@Param("postId") Long postId);
    
    @Insert("INSERT INTO post (user_id, category_id, title, content, post_type, bounty_amount, " +
            "bounty_status, emergency_level, status, created_at, updated_at) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, #{content}, #{postType}, #{bountyAmount}, " +
            "#{bountyStatus}, #{emergencyLevel}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    default int insert(Post post) {
        Logger logger = LoggerFactory.getLogger(PostMapper.class);
        logger.info("执行插入帖子SQL，参数: userId={}, categoryId={}, title={}, content={}, postType={}", 
            post.getUserId(), post.getCategoryId(), post.getTitle(), post.getContent(), post.getPostType());
        return insertPost(post);
    }
    
    @Insert("INSERT INTO post (user_id, category_id, title, content, post_type, bounty_amount, " +
            "bounty_status, emergency_level, status, created_at, updated_at) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, #{content}, #{postType}, #{bountyAmount}, " +
            "#{bountyStatus}, #{emergencyLevel}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    int insertPost(Post post);

    @Select("SELECT * FROM post WHERE post_id = #{postId}")
    Post selectById(Long postId);

    @Select("SELECT p.* FROM post p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "WHERE p.category_id = #{categoryId} OR c.parent_id = #{categoryId} " +
            "ORDER BY p.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Post> selectByCategory(@Param("categoryId") Integer categoryId, 
                              @Param("offset") int offset, 
                              @Param("pageSize") int pageSize);

    @Select("SELECT * FROM post WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Post> selectByUser(@Param("userId") Long userId, 
                          @Param("offset") int offset, 
                          @Param("pageSize") int pageSize);

    @Update("UPDATE post SET status = #{status}, updated_at = NOW() WHERE post_id = #{postId}")
    int updateStatus(@Param("postId") Long postId, @Param("status") String status);

    @Update("UPDATE post SET view_count = view_count + 1 WHERE post_id = #{postId}")
    int incrementViewCount(Long postId);

    @Update("UPDATE post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    int incrementLikeCount(Long postId);

    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE post_id = #{postId}")
    int incrementCommentCount(Long postId);

    @Update("UPDATE post SET share_count = share_count + 1 WHERE post_id = #{postId}")
    int incrementShareCount(Long postId);

    @Select("SELECT * FROM post " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Post> selectList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Update("UPDATE post SET user_id = #{userId}, category_id = #{categoryId}, " +
            "title = #{title}, content = #{content}, post_type = #{postType}, " +
            "bounty_amount = #{bountyAmount}, bounty_status = #{bountyStatus}, " +
            "emergency_level = #{emergencyLevel}, " +
            "status = #{status}, updated_at = NOW() " +
            "WHERE post_id = #{postId}")
    int updateById(Post post);

    @Delete("DELETE FROM post WHERE post_id = #{postId}")
    int deleteById(Long postId);

    /**
     * 滚动分页查询帖子列表
     * @param params 查询参数
     * @return 帖子列表
     */
    List<PostVO> selectForScroll(Map<String, Object> params);

    /**
     * 根据分类滚动分页查询帖子列表
     *
     * @param params 查询参数，包含 categoryId、pageSize 和可选的 lastTime
     * @return 帖子VO列表
     */
    List<PostVO> selectByCategoryForScroll(Map<String, Object> params);
    
    /**
     * 根据用户滚动分页查询帖子列表
     *
     * @param params 查询参数，包含 userId、pageSize 和可选的 lastTime
     * @return 帖子VO列表
     */
    List<PostVO> selectByUserForScroll(Map<String, Object> params);

    /**
     * 按时间倒序查询帖子列表
     *
     * @param lastTime 最后一条记录的时间戳
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    List<PostVO> selectPostsByTime(@Param("lastTime") Date lastTime, @Param("pageSize") int pageSize);

    /**
     * 瀑布流加载帖子列表
     * 按时间倒序查询，支持时间戳分页
     *
     * @param params 查询参数，包含 status、limit 和可选的 lastTime
     * @return 帖子列表
     */
    List<PostVO> getPostsWaterfall(Map<String, Object> params);

    /**
     * 根据用户ID分页查询帖子列表
     *
     * @param params 查询参数，包含userId、offset和pageSize
     * @return 帖子列表
     */
    List<PostVO> selectByUser(Map<String, Object> params);

    /**
     * 根据状态查询所有帖子
     *
     * @param status 帖子状态
     * @return 帖子列表
     */
    @Select("SELECT * FROM post WHERE status = #{status}")
    List<Post> findAllByStatus(PostStatusEnum status);
    
    /**
     * 根据ID列表批量查询帖子VO
     *
     * @param postIds 帖子ID列表
     * @return 帖子VO列表
     */
    @Select("<script>" +
            "SELECT p.*, c.name as category_name, u.username, u.avatar " +
            "FROM post p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "LEFT JOIN student_user u ON p.user_id = u.user_id " +
            "WHERE p.post_id IN " +
            "<foreach collection='postIds' item='postId' open='(' separator=',' close=')'>" +
            "#{postId}" +
            "</foreach>" +
            " ORDER BY FIELD(p.post_id, " +
            "<foreach collection='postIds' item='postId' separator=','>" +
            "#{postId}" +
            "</foreach>" +
            ")" +
            "</script>")
    List<PostVO> findPostVOsByIds(@Param("postIds") List<Long> postIds);

    /**
     * 获取热点帖子列表
     *
     * @param limit 获取条数
     * @return 热点帖子列表
     */
    @Select("SELECT p.*, u.username, i.file_path as avatar, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN student_user u ON p.user_id = u.user_id " +
            "LEFT JOIN image i ON u.avatar_image_id = i.id " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "WHERE p.status = 'PUBLISHED' " +
            "ORDER BY p.hot_score DESC " +
            "LIMIT #{limit}")
    List<PostVO> getHotPosts(@Param("limit") int limit);

    /**
     * 更新帖子热度分数
     *
     * @param postId 帖子ID
     * @param hotScore 热度分数
     * @return 影响行数
     */
    @Update("UPDATE post SET hot_score = #{hotScore}, last_hot_calc = NOW() " +
            "WHERE post_id = #{postId}")
    int updateHotScore(@Param("postId") Long postId, @Param("hotScore") BigDecimal hotScore);

    /**
     * 获取需要更新热度的帖子列表
     *
     * @param lastCalcTime 上次计算时间
     * @return 帖子列表
     */
    @Select("SELECT * FROM post " +
            "WHERE status = 'PUBLISHED' " +
            "AND (last_hot_calc IS NULL OR last_hot_calc < #{lastCalcTime})")
    List<Post> getPostsNeedHotCalc(@Param("lastCalcTime") LocalDateTime lastCalcTime);

    /**
     * 获取5天内按热度排序的热点帖子
     * @param fromTime 起始时间（5天前）
     * @param limit 获取条数
     * @return 热点帖子列表
     */
    @Select("SELECT p.*, u.username, i.file_path as avatar, c.name as category_name " +
            "FROM post p " +
            "LEFT JOIN student_user u ON p.user_id = u.user_id " +
            "LEFT JOIN image i ON u.avatar_image_id = i.id " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "WHERE p.status = 'PUBLISHED' AND p.created_at >= #{fromTime} " +
            "ORDER BY p.hot_score DESC LIMIT #{limit}")
    List<PostVO> getLatestHotPosts(@Param("fromTime") java.time.LocalDateTime fromTime, @Param("limit") int limit);
}