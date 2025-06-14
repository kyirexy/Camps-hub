package com.liuxy.campushub.mapper;

import com.liuxy.campushub.entity.LostFound;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 失物招领数据访问层
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Mapper
public interface LostFoundMapper {
    
    /**
     * 创建失物招领记录
     *
     * @param lostFound 失物招领实体
     * @return 影响行数
     */
    @Insert("INSERT INTO lost_found (post_id, item_type, item_name, lost_time, lost_location, " +
            "description, contact_info, reward_amount, found_time) " +
            "VALUES (#{postId}, #{itemType}, #{itemName}, #{lostTime}, " +
            "ST_GeomFromText(POINT(#{longitude}, #{latitude})), #{description}, " +
            "#{contactInfo}, #{rewardAmount}, #{foundTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LostFound lostFound);
    
    /**
     * 更新失物招领信息
     *
     * @param lostFound 失物招领实体
     * @return 影响行数
     */
    @Update("UPDATE lost_found SET item_type = #{itemType}, item_name = #{itemName}, " +
            "lost_time = #{lostTime}, lost_location = ST_GeomFromText(POINT(#{longitude}, #{latitude})), " +
            "description = #{description}, contact_info = #{contactInfo}, " +
            "reward_amount = #{rewardAmount}, found_time = #{foundTime} " +
            "WHERE post_id = #{postId}")
    int updateById(LostFound lostFound);
    
    /**
     * 根据帖子ID查询失物招领信息
     *
     * @param postId 帖子ID
     * @return 失物招领实体
     */
    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found WHERE post_id = #{postId}")
    LostFound selectByPostId(Long postId);
    
    /**
     * 更新找回时间
     *
     * @param postId 帖子ID
     * @return 影响行数
     */
    @Update("UPDATE lost_found SET found_time = NOW() WHERE post_id = #{postId}")
    int updateFoundTime(Long postId);
    
    /**
     * 搜索失物招领信息
     *
     * @param keyword 关键词
     * @param offset 偏移量
     * @param pageSize 限制数量
     * @return 失物招领列表
     */
    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found " +
            "WHERE MATCH(item_name, description) AGAINST(#{keyword} IN BOOLEAN MODE) " +
            "ORDER BY lost_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> search(@Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);
    
    /**
     * 查询未找回的失物招领信息
     *
     * @param offset 偏移量
     * @param pageSize 限制数量
     * @return 失物招领列表
     */
    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found WHERE found_time IS NULL " +
            "ORDER BY lost_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> selectUnfound(@Param("offset") int offset,
                                 @Param("pageSize") int pageSize);
    
    /**
     * 查询已找回的失物招领信息
     *
     * @param offset 偏移量
     * @param pageSize 限制数量
     * @return 失物招领列表
     */
    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found WHERE found_time IS NOT NULL " +
            "ORDER BY found_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> selectFound(@Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found " +
            "WHERE ST_Distance_Sphere(lost_location, ST_GeomFromText(POINT(#{longitude}, #{latitude}))) <= #{radius} " +
            "ORDER BY lost_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> selectNearby(@Param("longitude") double longitude,
                                @Param("latitude") double latitude,
                                @Param("radius") int radius,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);

    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found WHERE item_type = #{itemType} " +
            "ORDER BY lost_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> selectByItemType(@Param("itemType") String itemType,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    @Select("SELECT *, ST_X(lost_location) as longitude, ST_Y(lost_location) as latitude " +
            "FROM lost_found " +
            "WHERE lost_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY lost_time DESC LIMIT #{offset}, #{pageSize}")
    List<LostFound> selectByTimeRange(@Param("startTime") String startTime,
                                     @Param("endTime") String endTime,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);
} 