package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.LostFound;
import com.liuxy.campushub.service.LostFoundService;
import com.liuxy.campushub.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 失物招领控制器
 *
 * @author liuxy
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/api/lost-found")
public class LostFoundController {
    
    @Autowired
    private LostFoundService lostFoundService;
    
    /**
     * 创建失物招领记录
     *
     * @param lostFound 失物招领信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Boolean> createLostFound(@RequestBody LostFound lostFound) {
        try {
            boolean success = lostFoundService.createLostFound(lostFound);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("创建失物招领记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新失物招领信息
     *
     * @param postId 帖子ID
     * @param lostFound 失物招领信息
     * @return 更新结果
     */
    @PutMapping("/{postId}")
    public Result<Boolean> updateLostFound(@PathVariable Long postId, @RequestBody LostFound lostFound) {
        try {
            lostFound.setPostId(postId);
            boolean updated = lostFoundService.updateLostFound(lostFound);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新失物招领信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取失物招领详情
     *
     * @param postId 帖子ID
     * @return 失物招领详情
     */
    @GetMapping("/{postId}")
    public Result<LostFound> getLostFound(@PathVariable Long postId) {
        try {
            LostFound lostFound = lostFoundService.getLostFoundByPostId(postId);
            if (lostFound == null) {
                return Result.error("失物招领记录不存在");
            }
            return Result.success(lostFound);
        } catch (Exception e) {
            return Result.error("获取失物招领详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新找到时间
     *
     * @param postId 帖子ID
     * @return 更新结果
     */
    @PutMapping("/{postId}/found")
    public Result<Boolean> updateFoundTime(@PathVariable Long postId) {
        try {
            // 使用当前时间作为找到时间
            String foundTime = java.time.LocalDateTime.now().toString();
            boolean updated = lostFoundService.updateFoundTime(postId, foundTime);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新找到时间失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索失物招领
     *
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/search")
    public Result<List<LostFound>> searchLostFound(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.searchLostFound(keyword, pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("搜索失物招领失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未找到的物品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/unfound")
    public Result<List<LostFound>> getUnfoundItems(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.getUnfoundItems(pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("获取未找到物品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取已找到的物品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/found")
    public Result<List<LostFound>> getFoundItems(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.getFoundItems(pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("获取已找到物品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取附近的失物招领
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径（米）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/nearby")
    public Result<List<LostFound>> getNearbyLostFound(@RequestParam double longitude,
                                                    @RequestParam double latitude,
                                                    @RequestParam(defaultValue = "1000") int radius,
                                                    @RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.getNearbyLostFound(longitude, latitude, radius, pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("获取附近失物招领失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据物品类型查询
     *
     * @param itemType 物品类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/type/{itemType}")
    public Result<List<LostFound>> getLostFoundByItemType(@PathVariable String itemType,
                                                        @RequestParam(defaultValue = "1") int pageNum,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.getLostFoundByItemType(itemType, pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("获取物品类型列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据时间范围查询
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 失物招领列表
     */
    @GetMapping("/time-range")
    public Result<List<LostFound>> getLostFoundByTimeRange(@RequestParam String startTime,
                                                         @RequestParam String endTime,
                                                         @RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<LostFound> lostFounds = lostFoundService.getLostFoundByTimeRange(startTime, endTime, pageNum, pageSize);
            return Result.success(lostFounds);
        } catch (Exception e) {
            return Result.error("获取时间范围列表失败: " + e.getMessage());
        }
    }
}