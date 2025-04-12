package com.liuxy.campushub.vo;

import lombok.Data;
import java.util.Date;

/**
 * 帖子展示视图对象
 */
@Data
public class PostVO {
    private Long postId;
    private String title;
    private String categoryName;
    private String postType;
    private Double bountyAmount;
    private String emergencyLevel;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Date createdAt;
    
    // 用户相关信息
    private Long userId;
    private String username;
    private String avatar;
} 