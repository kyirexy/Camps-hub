package com.liuxy.campushub.mq;

import com.liuxy.campushub.config.RabbitMQConfig;
import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.model.HotPostModel;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.vo.HotPostVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HotPostUpdateConsumer {

    private static final String HOT_POSTS_KEY = "hot:posts";
    private static final String HOT_POST_DETAIL_KEY = "hot:post:detail:";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PostService postService;
    
    @RabbitListener(queues = RabbitMQConfig.HOT_POST_QUEUE)
    public void handleHotPostUpdate(Long postId) {
        try {
            log.info("收到更新热度消息，postId: {}", postId);
            
            // 获取帖子信息
            Post post = postService.getPostById(postId);
            if (post == null) {
                log.warn("帖子不存在，postId: {}", postId);
                return;
            }
            
            // 计算热度
            double hotness = HotPostModel.calculateHotness(post);
            
            // 更新Redis中的热度分数
            redisTemplate.opsForZSet().add(HOT_POSTS_KEY, postId, hotness);
            
            // 更新帖子详情缓存
            HotPostVO hotPostVO = HotPostVO.fromPost(post);
            redisTemplate.opsForValue().set(HOT_POST_DETAIL_KEY + postId, hotPostVO);
            
            log.info("更新热度成功，postId: {}, hotness: {}", postId, hotness);
        } catch (Exception e) {
            log.error("处理更新热度消息失败，postId: {}", postId, e);
        }
    }
} 