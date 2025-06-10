package com.liuxy.campushub.vo;

import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.model.HotPostModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 热点帖子VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotPostVO extends PostVO {
    
    /**
     * 热度值
     */
    private Double hotness;
    
    /**
     * 排名
     */
    private Integer rank;
    
    /**
     * 是否为新发布（24小时内）
     */
    private Boolean isNew;
    
    /**
     * 是否为突发热点
     */
    private Boolean isBurst;
    
    /**
     * 从Post实体转换为HotPostVO
     */
    public static HotPostVO fromPost(Post post) {
        HotPostVO vo = new HotPostVO();
        // 复制基本属性
        BeanUtils.copyProperties(post, vo);
        
        // 计算热度相关属性
        vo.setHotness(HotPostModel.calculateHotness(post));
        vo.setIsNew(HotPostModel.isNewPost(post));
        vo.setIsBurst(HotPostModel.isBurstHot(post));
        
        return vo;
    }
    
    /**
     * 从PostVO转换为HotPostVO
     */
    public static HotPostVO fromPostVO(PostVO postVO) {
        HotPostVO vo = new HotPostVO();
        // 复制基本属性
        BeanUtils.copyProperties(postVO, vo);
        
        // 设置默认值
        vo.setIsNew(false);
        vo.setIsBurst(false);
        
        return vo;
    }
} 