package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.LostFound;
import com.liuxy.campushub.mapper.LostFoundMapper;
import com.liuxy.campushub.service.LostFoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 失物招领服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class LostFoundServiceImpl implements LostFoundService {

    @Autowired
    private LostFoundMapper lostFoundMapper;

    @Override
    @Transactional
    public boolean createLostFound(LostFound lostFound) {
        return lostFoundMapper.insert(lostFound) > 0;
    }

    @Override
    @Transactional
    public boolean updateLostFound(LostFound lostFound) {
        return lostFoundMapper.updateById(lostFound) > 0;
    }

    @Override
    public LostFound getLostFoundByPostId(Long postId) {
        return lostFoundMapper.selectByPostId(postId);
    }

    @Override
    @Transactional
    public boolean updateFoundTime(Long postId, String foundTime) {
        // foundTime参数在接口中定义但在Mapper实现中不需要
        // Mapper的updateFoundTime方法使用NOW()函数设置当前时间
        return lostFoundMapper.updateFoundTime(postId) > 0;
    }

    @Override
    public List<LostFound> searchLostFound(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.search(keyword, offset, pageSize);
    }

    @Override
    public List<LostFound> getUnfoundItems(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.selectUnfound(offset, pageSize);
    }

    @Override
    public List<LostFound> getFoundItems(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.selectFound(offset, pageSize);
    }

    @Override
    public List<LostFound> getNearbyLostFound(double longitude, double latitude, int radius, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.selectNearby(longitude, latitude, radius, offset, pageSize);
    }

    @Override
    public List<LostFound> getLostFoundByItemType(String itemType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.selectByItemType(itemType, offset, pageSize);
    }

    @Override
    public List<LostFound> getLostFoundByTimeRange(String startTime, String endTime, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return lostFoundMapper.selectByTimeRange(startTime, endTime, offset, pageSize);
    }
}