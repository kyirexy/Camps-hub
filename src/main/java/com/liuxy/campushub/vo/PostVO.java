package com.liuxy.campushub.vo;

import com.liuxy.campushub.enums.PostTypeEnum;
import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 帖子展示视图对象
 */
@Data
public class PostVO {
    private Long postId;
    private Long userId;
    private String title;
    private String content;
    private String categoryName;
    private PostTypeEnum postType;
    private BigDecimal bountyAmount;
    private Integer emergencyLevel;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Date createdAt;
    
    // 用户相关信息
    private String username;
    private String avatar;
} 