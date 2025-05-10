package com.liuxy.campushub.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 热点帖子视图对象
 * 
 * @author liuxy
 * @since 2024-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotPostVO extends PostVO {
    
    /**
     * 热度值
     */
    private double hotness;
    
    /**
     * 是否为新发布（24小时内）
     */
    private boolean isNew;
    
    /**
     * 是否为突发热点
     */
    private boolean isBurst;
    
    /**
     * 热度排名
     */
    private int rank;
} 