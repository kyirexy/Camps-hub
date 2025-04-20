package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.*;
import com.liuxy.campushub.enums.BountyStatusEnum;
import com.liuxy.campushub.enums.PostTypeEnum;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.service.TopicService;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.service.LostFoundService;
import com.liuxy.campushub.common.Result;
import com.liuxy.campushub.vo.PostVO;
import com.liuxy.campushub.vo.ScrollResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 帖子控制器
 *
 * @author liuxy
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private static boolean checkStatusTransition(String newStatus, String currentStatus) {
        Map<String, List<String>> allowedTransitions = Map.of(
            "open", List.of("in_progress", "closed"),
            "in_progress", List.of("closed"),
            "closed", List.of("reopened")
        );
        return allowedTransitions.getOrDefault(currentStatus, Collections.emptyList()).contains(newStatus);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private TopicService topicService;
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private LostFoundService lostFoundService;
    
    /**
     * 创建新帖子
     *
     * @param request 请求体，包含帖子信息和base64图片列表
     * @return 创建结果
     */
    @PostMapping
    public Result<Long> createPost(@RequestBody Map<String, Object> request) {
        try {
            logger.info("收到创建帖子请求，请求参数: {}", request);
            
            // 验证必要参数
            if (!request.containsKey("title") || !request.containsKey("content") 
                    || !request.containsKey("postType") || !request.containsKey("categoryId")) {
                    logger.error("缺少必要参数，当前参数: {}", request.keySet());
                    return Result.error("缺少必要参数");
                }
            
            // 悬赏类型额外校验
            if ("bounty".equals(request.get("postType"))) {
                if (!request.containsKey("bountyAmount") || !request.containsKey("emergencyLevel")) {
                    logger.error("悬赏类型缺少必要参数: bountyAmount={}, emergencyLevel={}", 
                        request.containsKey("bountyAmount"), request.containsKey("emergencyLevel"));
                    return Result.error("悬赏类型需要填写金额和紧急程度");
                }
                
                try {
                    new BigDecimal(request.get("bountyAmount").toString());
                } catch (Exception e) {
                    logger.error("悬赏金额格式错误: {}", request.get("bountyAmount"));
                    return Result.error("悬赏金额必须是有效数字");
                }
            }
            
            // 从安全上下文获取当前用户ID
            Long userId;
            try {
                userId = com.liuxy.campushub.utils.SecurityUtil.getCurrentUserId();
                logger.info("从安全上下文获取到当前用户ID: {}", userId);
            } catch (Exception e) {
                logger.error("获取当前用户ID失败，尝试从请求中获取", e);
                // 如果从安全上下文获取失败，尝试从请求中获取
                if (request.containsKey("userId")) {
                    userId = Long.valueOf(request.get("userId").toString());
                    logger.info("从请求中获取到用户ID: {}", userId);
                } else {
                    logger.error("无法获取用户ID，请求中也不包含userId参数");
                    return Result.error("无法获取用户ID");
                }
            }
            
            Post post = new Post();
            post.setTitle((String) request.get("title"));
            post.setContent((String) request.get("content"));
            
            // 更新悬赏字段
            if ("bounty".equals(request.get("postType"))) {
                post.setBountyAmount(new BigDecimal(request.get("bountyAmount").toString()));
                post.setEmergencyLevel(Integer.valueOf(request.get("emergencyLevel").toString()));
                
                // 状态变更校验
                if (request.containsKey("bountyStatus")) {
                    String newStatus = (String) request.get("bountyStatus");
                    // 创建流程中postId尚未生成，状态校验应在更新方法中进行
                    post.setBountyStatus(BountyStatusEnum.valueOf(newStatus));
                }
            }
            post.setUserId(userId);
            post.setPostType(PostTypeEnum.valueOf((String) request.get("postType")));
            post.setCategoryId(Integer.valueOf(request.get("categoryId").toString()));

            // 设置悬赏相关字段
            if ("bounty".equals(post.getPostType())) {
                post.setBountyAmount(new BigDecimal(request.get("bountyAmount").toString()));
                post.setEmergencyLevel(Integer.valueOf(request.get("emergencyLevel").toString()));
                post.setBountyStatus(BountyStatusEnum.valueOf("open")); // 默认状态为开放
            }
            
            logger.info("创建Post对象: {}", post);
            
            // 获取base64图片列表
            @SuppressWarnings("unchecked")
            List<String> base64Images = (List<String>) request.get("images");
            
            // 创建帖子
            Long postId = postService.createPost(post, base64Images);
            if (postId == null) {
                logger.error("创建帖子失败，返回的postId为null");
                return Result.error("创建帖子失败");
            }
            
            logger.info("帖子创建成功，postId: {}", postId);
            
            // 处理话题
            @SuppressWarnings("unchecked")
            List<String> topicNames = (List<String>) request.get("topicNames");
            if (topicNames != null && !topicNames.isEmpty()) {
                logger.info("处理帖子话题，topicNames: {}", topicNames);
                for (String topicName : topicNames) {
                    topicService.getOrCreateTopic(topicName, post.getUserId());
                }
                List<Integer> topicIds = topicNames.stream()
                    .map(name -> topicService.getTopicByName(name).getTopicId())
                    .toList();
                topicService.batchLinkPostTopic(postId, topicIds);
            }
            
            // 如果是失物招领类型，创建失物招领记录
            if ("lost".equals(post.getPostType())) {
                logger.info("创建失物招领记录，postId: {}", postId);
                LostFound lostFound = new LostFound();
                lostFound.setPostId(postId);
                lostFound.setFoundTime(null);
                lostFoundService.createLostFound(lostFound);
            }
            
            return Result.success(postId);
        } catch (Exception e) {
            logger.error("创建帖子失败，详细错误信息: ", e);
            logger.error("请求参数: {}", request);
            // 获取完整的堆栈跟踪信息
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace.append("\n    at ").append(element.toString());
            }
            logger.error("异常堆栈: {}", stackTrace.toString());
            return Result.error("创建帖子失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新帖子信息
     *
     * @param postId 帖子ID
     * @param request 请求体，包含帖子信息和base64图片列表
     * @return 更新结果
     */
    @PutMapping("/{postId}")
    public Result<Boolean> updatePost(@PathVariable Long postId, @RequestBody Map<String, Object> request) {
        try {
            logger.info("收到更新帖子请求，postId: {}，请求参数: {}", postId, request);
            
            // 基础参数校验
            if (!request.containsKey("title") || !request.containsKey("content")) {
                logger.error("缺少必要参数，当前参数: {}", request.keySet());
                return Result.error("缺少必要参数");
            }
            
            // 悬赏类型校验
            if ("bounty".equals(request.get("postType"))) {
                if (!request.containsKey("bountyAmount") || !request.containsKey("emergencyLevel")) {
                    logger.error("悬赏类型缺少必要参数: bountyAmount={}, emergencyLevel={}", 
                        request.containsKey("bountyAmount"), request.containsKey("emergencyLevel"));
                    return Result.error("悬赏类型需要填写金额和紧急程度");
                }
                
                try {
                    new BigDecimal(request.get("bountyAmount").toString());
                } catch (Exception e) {
                    logger.error("悬赏金额格式错误: {}", request.get("bountyAmount"));
                    return Result.error("悬赏金额必须是有效数字");
                }
            }
            
            // 从安全上下文获取当前用户ID
            Long userId;
            try {
                userId = com.liuxy.campushub.utils.SecurityUtil.getCurrentUserId();
                logger.info("从安全上下文获取到当前用户ID: {}", userId);
            } catch (Exception e) {
                logger.error("获取当前用户ID失败，尝试从请求中获取", e);
                // 如果从安全上下文获取失败，尝试从请求中获取
                if (request.containsKey("userId")) {
                    userId = Long.valueOf(request.get("userId").toString());
                    logger.info("从请求中获取到用户ID: {}", userId);
                } else {
                    logger.warn("无法获取用户ID，使用默认值null");
                    userId = null;
                }
            }
            
            Post post = new Post();
            post.setPostId(postId);
            post.setTitle((String) request.get("title"));
            post.setContent((String) request.get("content"));
            
            // 更新悬赏字段
            if ("bounty".equals(request.get("postType"))) {
                post.setBountyAmount(new BigDecimal(request.get("bountyAmount").toString()));
                post.setEmergencyLevel(Integer.valueOf(request.get("emergencyLevel").toString()));
                
                // 状态变更校验
                if (request.containsKey("bountyStatus")) {
                    String newStatus = (String) request.get("bountyStatus");
                    // 创建流程中postId尚未生成，状态校验应在更新方法中进行
                    post.setBountyStatus(BountyStatusEnum.valueOf(newStatus));
                }
            }
            post.setUserId(userId); // 设置用户ID，即使为null也设置，由服务层处理
            
            // 获取base64图片列表
            @SuppressWarnings("unchecked")
            List<String> base64Images = (List<String>) request.get("images");
            
            // 更新帖子
            boolean updated = postService.updatePost(post, base64Images);
            
            // 处理话题
            @SuppressWarnings("unchecked")
            List<String> topicNames = (List<String>) request.get("topicNames");
            if (topicNames != null) {
                // 先解除所有现有关联
                List<Integer> existingTopicIds = topicService.getTopicsByPostId(postId).stream()
                    .map(Topic::getTopicId)
                    .toList();
                for (Integer topicId : existingTopicIds) {
                    topicService.unlinkPostTopic(postId, topicId);
                }
                
                // 建立新的关联
                for (String topicName : topicNames) {
                    topicService.getOrCreateTopic(topicName, post.getUserId());
                }
                List<Integer> newTopicIds = topicNames.stream()
                    .map(name -> topicService.getTopicByName(name).getTopicId())
                    .toList();
                topicService.batchLinkPostTopic(postId, newTopicIds);
            }
            
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新帖子失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @return 帖子详情
     */
    @GetMapping("/{postId}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            if (post == null) {
                return Result.error("帖子不存在");
            }
            
            // 增加浏览量
            postService.incrementViewCount(postId);
            
            // 获取帖子关联的话题
            List<Topic> topics = topicService.getTopicsByPostId(postId);
            
            // 获取帖子附件
            List<Attachment> attachments = attachmentService.getAttachmentsByPostId(postId);
            
            // 如果是失物招领类型，获取失物招领信息
            LostFound lostFound = null;
            if ("lost".equals(post.getPostType())) {
                lostFound = lostFoundService.getLostFoundByPostId(postId);
            }
            
            // 获取帖子详情
            Map<String, Object> result = new HashMap<>();
            result.put("post", post);
            result.put("topics", topics);
            result.put("attachments", attachments);
            result.put("lostFound", lostFound);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取帖子详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新帖子状态
     *
     * @param postId 帖子ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{postId}/status")
    public Result<Boolean> updatePostStatus(@PathVariable Long postId, @RequestParam String status) {
        try {
            boolean updated = postService.updatePostStatus(postId, status);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error("更新帖子状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞帖子
     *
     * @param postId 帖子ID
     * @return 操作结果
     */
    @PostMapping("/{postId}/like")
    public Result<Boolean> likePost(@PathVariable Long postId) {
        try {
            boolean success = postService.incrementLikeCount(postId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }
    
    /**
     * 分享帖子
     *
     * @param postId 帖子ID
     * @return 操作结果
     */
    @PostMapping("/{postId}/share")
    public Result<Boolean> sharePost(@PathVariable Long postId) {
        try {
            boolean success = postService.incrementShareCount(postId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("分享失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @return 删除结果
     */
    @DeleteMapping("/{postId}")
    public Result<Boolean> deletePost(@PathVariable Long postId) {
        try {
            boolean success = postService.deletePost(postId);
            return Result.success(success);
        } catch (Exception e) {
            logger.error("删除帖子失败", e);
            return Result.error("删除帖子失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取帖子列表（滚动分页）
     *
     * @param lastTime 最后一条记录的时间戳（首次传空）
     * @param pageSize 每页数量，默认10
     * @return 帖子列表
     */
    @GetMapping
    public Result<ScrollResult<PostVO>> getPostList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastTime,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            ScrollResult<PostVO> result = postService.getPostList(lastTime, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取帖子列表失败", e);
            return Result.error("获取帖子列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 瀑布流加载帖子列表
     *
     * @param lastTime 最后一条记录的时间戳（首次加载传null）
     * @param limit 获取条数，默认10条
     * @return 帖子列表
     */
    @GetMapping("/waterfall")
    public Result<ScrollResult<PostVO>> getPostsWaterfall(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastTime,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            ScrollResult<PostVO> result = postService.getPostsWaterfall(lastTime, limit);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("瀑布流加载失败", e);
            return Result.error("瀑布流加载失败: " + e.getMessage());
        }
    }
}