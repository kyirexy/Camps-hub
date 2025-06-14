package com.liuxy.campushub.service;

import com.liuxy.campushub.vo.HotPostVO;
import java.util.List;

public interface HotPostService {
    
    /**
     * 获取热点帖子列表
     *
     * @param limit 获取条数
     * @return 热点帖子列表
     */
    List<HotPostVO> getHotPosts(int limit);
    
    /**
     * 更新帖子热度
     *
     * @param postId 帖子ID
     */
    void updatePostHotness(Long postId);
    
    /**
     * 批量更新帖子热度
     *
     * @param postIds 帖子ID列表
     */
    void batchUpdatePostHotness(List<Long> postIds);
} 