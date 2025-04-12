package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.Topic;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 话题数据访问层
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface TopicMapper {
    
    /**
     * 创建新话题
     *
     * @param topic 话题实体
     * @return 影响行数
     */
    @Insert("INSERT INTO topic (topic_name, creator_id, usage_count, created_at) " +
            "VALUES (#{topicName}, #{creatorId}, #{usageCount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "topicId")
    int insert(Topic topic);
    
    /**
     * 根据ID查询话题
     *
     * @param topicId 话题ID
     * @return 话题实体
     */
    @Select("SELECT * FROM topic WHERE topic_id = #{topicId}")
    Topic selectById(Integer topicId);
    
    /**
     * 根据名称查询话题
     *
     * @param topicName 话题名称
     * @return 话题实体
     */
    @Select("SELECT * FROM topic WHERE topic_name = #{topicName}")
    Topic selectByName(String topicName);
    
    /**
     * 查询热门话题
     *
     * @param limit 限制数量
     * @return 话题列表
     */
    @Select("SELECT * FROM topic ORDER BY usage_count DESC LIMIT #{limit}")
    List<Topic> selectHotTopics(int limit);
    
    /**
     * 增加话题使用次数
     *
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE topic SET usage_count = usage_count + 1 WHERE topic_id = #{topicId}")
    int incrementUsageCount(Integer topicId);
    
    /**
     * 关联帖子与话题
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Insert("INSERT INTO post_topic (post_id, topic_id) VALUES (#{postId}, #{topicId})")
    int insertPostTopic(@Param("postId") Long postId, @Param("topicId") Integer topicId);
    
    /**
     * 批量关联帖子与话题
     *
     * @param postId 帖子ID
     * @param topicIds 话题ID列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO post_topic (post_id, topic_id) VALUES " +
            "<foreach collection='topicIds' item='topicId' separator=','>" +
            "(#{postId}, #{topicId})" +
            "</foreach>" +
            "</script>")
    int batchInsertPostTopic(@Param("postId") Long postId, @Param("topicIds") List<Long> topicIds);
    
    /**
     * 解除帖子与话题的关联
     *
     * @param postId 帖子ID
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Delete("DELETE FROM post_topic WHERE post_id = #{postId} AND topic_id = #{topicId}")
    int deletePostTopic(@Param("postId") Long postId, @Param("topicId") Integer topicId);
    
    /**
     * 查询帖子关联的所有话题
     *
     * @param postId 帖子ID
     * @return 话题列表
     */
    @Select("SELECT t.* FROM topic t " +
            "INNER JOIN post_topic pt ON t.topic_id = pt.topic_id " +
            "WHERE pt.post_id = #{postId}")
    List<Topic> selectByPostId(Long postId);
    
    /**
     * 搜索话题
     *
     * @param keyword 关键词
     * @param offset 偏移量
     * @param pageSize 限制数量
     * @return 话题列表
     */
    @Select("SELECT * FROM topic " +
            "WHERE topic_name LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY usage_count DESC LIMIT #{offset}, #{pageSize}")
    List<Topic> search(@Param("keyword") String keyword,
                      @Param("offset") int offset,
                      @Param("pageSize") int pageSize);
} 