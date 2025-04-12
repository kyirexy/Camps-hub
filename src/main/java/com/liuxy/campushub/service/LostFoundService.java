package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.LostFound;
import java.util.List;

/**
 * 失物招领服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface LostFoundService {
    
    /**
     * 创建失物招领记录
     *
     * @param lostFound 失物招领实体
     * @return 是否创建成功
     */
    boolean createLostFound(LostFound lostFound);
    
    /**
     * 更新失物招领信息
     *
     * @param lostFound 失物招领实体
     * @return 是否更新成功
     */
    boolean updateLostFound(LostFound lostFound);
    
    /**
     * 根据帖子ID查询失物招领信息
     *
     * @param postId 帖子ID
     * @return 失物招领实体
     */
    LostFound getLostFoundByPostId(Long postId);
    
    /**
     * 更新找回时间
     *
     * @param postId 帖子ID
     * @param foundTime 找回时间
     * @return 是否更新成功
     */
    boolean updateFoundTime(Long postId, String foundTime);
    
    /**
     * 搜索失物招领信息
     *
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> searchLostFound(String keyword, int pageNum, int pageSize);
    
    /**
     * 查询未找回的失物招领信息
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> getUnfoundItems(int pageNum, int pageSize);
    
    /**
     * 查询已找回的失物招领信息
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> getFoundItems(int pageNum, int pageSize);
    
    /**
     * 根据位置查询附近的失物招领信息
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径（米）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> getNearbyLostFound(double longitude, double latitude, int radius, int pageNum, int pageSize);
    
    /**
     * 根据物品类型查询失物招领信息
     *
     * @param itemType 物品类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> getLostFoundByItemType(String itemType, int pageNum, int pageSize);
    
    /**
     * 根据时间范围查询失物招领信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    List<LostFound> getLostFoundByTimeRange(String startTime, String endTime, int pageNum, int pageSize);
} 