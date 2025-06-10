package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.config.RabbitMQConfig;
import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.enums.PostTypeEnum;
import com.liuxy.campushub.model.HotPostModel;
import com.liuxy.campushub.service.HotPostService;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.vo.HotPostVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HotPostServiceImpl implements HotPostService {

    private static final String HOT_POSTS_KEY = "hot:posts";
    private static final String HOT_POST_DETAIL_KEY = "hot:post:detail:";
    
    // 注入图片访问基础URL
    @Value("${image.access.base-url:http://localhost:8081}") 
    private String imageBaseUrl;

    // 注入头像图片相对路径
    @Value("${image.access.path.avatars:/avatars}") 
    private String avatarPath;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private PostService postService;
    
    /**
     * 获取完整的头像URL
     */
    private String getCompleteAvatarUrl(String avatarFileName) {
        if (avatarFileName == null || avatarFileName.trim().isEmpty()) {
            return null;
        }
        // 拼接基础URL、头像路径和文件名
        return imageBaseUrl + avatarPath + "/" + avatarFileName;
    }
    
    @Override
    public List<HotPostVO> getHotPosts(int limit) {
        try {
            // 从Redis获取热点帖子ID列表
            Set<ZSetOperations.TypedTuple<Object>> hotPosts = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(HOT_POSTS_KEY, 0, limit - 1);
            
            if (hotPosts == null || hotPosts.isEmpty()) {
                // 如果缓存中没有数据，从数据库获取并更新缓存
                return refreshHotPostsCache(limit);
            }
            
            // 获取帖子详情
            List<HotPostVO> result = new ArrayList<>();
            for (ZSetOperations.TypedTuple<Object> tuple : hotPosts) {
                Long postId = Long.valueOf(tuple.getValue().toString());
                Double score = tuple.getScore();
                
                // 从Redis获取帖子详情
                Object cachedPost = redisTemplate.opsForValue().get(HOT_POST_DETAIL_KEY + postId);
                HotPostVO postDetail;
                
                if (cachedPost == null) {
                    // 如果缓存中没有详情，从数据库获取并更新缓存
                    Post post = postService.getPostById(postId);
                    if (post != null) {
                        postDetail = HotPostVO.fromPost(post);
                        // 设置完整的头像URL
                        if (post.getAvatar() != null) {
                            postDetail.setAvatar(getCompleteAvatarUrl(post.getAvatar()));
                        }
                        redisTemplate.opsForValue().set(HOT_POST_DETAIL_KEY + post.getPostId(), postDetail);
                    } else {
                        continue;
                    }
                } else {
                    // 将缓存中的对象转换为HotPostVO
                    postDetail = convertToHotPostVO(cachedPost);
                }
                
                if (postDetail != null) {
                    postDetail.setHotness(score);
                    result.add(postDetail);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取热点帖子列表失败", e);
            // 发生异常时从数据库获取
            return refreshHotPostsCache(limit);
        }
    }
    
    @Override
    public void updatePostHotness(Long postId) {
        try {
            // 发送消息到RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.HOT_POST_EXCHANGE,
                    RabbitMQConfig.HOT_POST_ROUTING_KEY,
                    postId
            );
            log.info("发送更新热度消息成功，postId: {}", postId);
        } catch (Exception e) {
            log.error("发送更新热度消息失败，postId: {}", postId, e);
        }
    }
    
    @Override
    public void batchUpdatePostHotness(List<Long> postIds) {
        try {
            // 批量发送消息到RabbitMQ
            for (Long postId : postIds) {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.HOT_POST_EXCHANGE,
                        RabbitMQConfig.HOT_POST_ROUTING_KEY,
                        postId
                );
            }
            log.info("批量发送更新热度消息成功，postIds: {}", postIds);
        } catch (Exception e) {
            log.error("批量发送更新热度消息失败，postIds: {}", postIds, e);
        }
    }
    
    /**
     * 刷新热点帖子缓存
     */
    private List<HotPostVO> refreshHotPostsCache(int limit) {
        try {
            // 从数据库获取最新的热点帖子，包含用户头像信息
            List<Post> posts = postService.getLatestPosts(limit);
            
            // 计算热度并更新Redis
            for (Post post : posts) {
                double hotness = HotPostModel.calculateHotness(post);
                
                // 更新热度分数
                redisTemplate.opsForZSet().add(HOT_POSTS_KEY, post.getPostId(), hotness);
                
                // 获取帖子详情，包含用户头像
                HotPostVO hotPostVO = HotPostVO.fromPost(post);
                // 设置完整的用户头像URL
                if (post.getUserId() != null && post.getAvatar() != null) {
                    hotPostVO.setAvatar(getCompleteAvatarUrl(post.getAvatar()));
                }
                redisTemplate.opsForValue().set(HOT_POST_DETAIL_KEY + post.getPostId(), hotPostVO);
            }
            
            // 设置过期时间（1小时）
            redisTemplate.expire(HOT_POSTS_KEY, java.time.Duration.ofHours(1));
            
            // 返回结果
            return posts.stream()
                    .map(post -> {
                        HotPostVO vo = HotPostVO.fromPost(post);
                        if (post.getUserId() != null && post.getAvatar() != null) {
                            vo.setAvatar(getCompleteAvatarUrl(post.getAvatar()));
                        }
                        return vo;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("刷新热点帖子缓存失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 将缓存中的对象转换为HotPostVO
     */
    private HotPostVO convertToHotPostVO(Object cachedPost) {
        if (cachedPost instanceof HotPostVO) {
            return (HotPostVO) cachedPost;
        }
        
        if (cachedPost instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) cachedPost;
            HotPostVO vo = new HotPostVO();
            
            // 设置基本属性
            vo.setPostId(((Number) map.get("postId")).longValue());
            vo.setUserId(((Number) map.get("userId")).longValue());
            vo.setTitle((String) map.get("title"));
            vo.setContent((String) map.get("content"));
            vo.setCategoryName((String) map.get("categoryName"));
            // 正确处理 PostTypeEnum
            String postTypeStr = (String) map.get("postType");
            if (postTypeStr != null) {
                vo.setPostType(PostTypeEnum.valueOf(postTypeStr));
            }
            vo.setBountyAmount(map.get("bountyAmount") != null ? 
                new BigDecimal(map.get("bountyAmount").toString()) : null);
            vo.setEmergencyLevel(((Number) map.get("emergencyLevel")).intValue());
            vo.setViewCount(((Number) map.get("viewCount")).intValue());
            vo.setLikeCount(((Number) map.get("likeCount")).intValue());
            vo.setCommentCount(((Number) map.get("commentCount")).intValue());
            vo.setShareCount(((Number) map.get("shareCount")).intValue());
            vo.setCreatedAt(map.get("createdAt") != null ? 
                new Date(((Number) map.get("createdAt")).longValue()) : null);
            vo.setUsername((String) map.get("username"));
            // 设置完整的头像URL
            String avatar = (String) map.get("avatar");
            if (avatar != null) {
                vo.setAvatar(getCompleteAvatarUrl(avatar));
            }
            vo.setHotness(map.get("hotness") != null ? 
                ((Number) map.get("hotness")).doubleValue() : null);
            vo.setRank(map.get("rank") != null ? 
                ((Number) map.get("rank")).intValue() : null);
            vo.setIsNew((Boolean) map.get("isNew"));
            vo.setIsBurst((Boolean) map.get("isBurst"));
            
            return vo;
        }
        
        return null;
    }
} 