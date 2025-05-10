package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.*;
import com.liuxy.campushub.enums.BountyStatusEnum;
import com.liuxy.campushub.enums.PostTypeEnum;
import com.liuxy.campushub.exception.BusinessException;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.service.TopicService;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.service.LostFoundService;
import com.liuxy.campushub.common.Result;
import com.liuxy.campushub.vo.PostDetailVO;
import com.liuxy.campushub.vo.PostVO;
import com.liuxy.campushub.vo.ScrollResult;
import com.liuxy.campushub.vo.HotPostVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.liuxy.campushub.utils.SecurityUtil.getCurrentUserId;

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
     * @param params 请求体，包含帖子信息和base64图片列表
     * @return 创建结果
     */
    @PostMapping
    public Result createPost(@RequestBody Map<String, Object> params) {
        try {
            logger.info("收到创建帖子请求，请求参数: {}", params);
            
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            logger.info("从安全上下文获取到当前用户ID: {}", userId);
            
            // 构建帖子对象
            Post post = new Post();
            post.setUserId(userId);
            post.setTitle((String) params.get("title"));
            post.setContent((String) params.get("content"));
            post.setCategoryId(Integer.valueOf(params.get("categoryId").toString()));
            
            // 处理帖子类型，转换为大写
            String postTypeStr = ((String) params.get("postType")).toUpperCase();
            post.setPostType(PostTypeEnum.valueOf(postTypeStr));
            
            // 处理话题
            @SuppressWarnings("unchecked")
            List<String> topicNames = (List<String>) params.get("topicNames");
            if (topicNames != null && !topicNames.isEmpty()) {
                List<Topic> topics = new ArrayList<>();
                for (String topicName : topicNames) {
                    Topic topic = topicService.getOrCreateTopic(topicName, userId);
                    topics.add(topic);
                }
                post.setTopics(topics);
            }
            
            // 处理图片
            @SuppressWarnings("unchecked")
            List<String> images = (List<String>) params.get("images");
            
            // 创建帖子
            Long postId = postService.createPost(post, images);
            
            return Result.success(postId);
        } catch (Exception e) {
            logger.error("创建帖子失败，详细错误信息: ", e);
            logger.error("请求参数: {}", params);
            logger.error("异常堆栈: ", e);
            return Result.error(500, "创建帖子失败: " + e.getMessage());
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
                userId = getCurrentUserId();
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
     * 获取帖子详情（重构版）
     *
     * @param postId 帖子ID 格式要求：大于0的正整数
     * @return 帖子详情响应实体，包含基础信息、话题及扩展字段
     */
    @GetMapping("/{postId}")
    public Result<PostDetailVO> getPostDetail(@PathVariable Long postId) {
        final String METHOD_NAME = "getPostDetail";
        logger.debug("{} 方法调用 - 请求参数: postId={}", METHOD_NAME, postId);

        try {
            // 参数预校验
            if (postId == null || postId <= 0) {
                logger.warn("{} 参数校验失败 - 无效的帖子ID: {}", METHOD_NAME, postId);
                return Result.error(400, "帖子ID格式错误");
            }

            // 核心业务逻辑
            Post post = postService.getPostById(postId);
            if (post == null) {
                logger.info("{} 数据不存在 - postId: {}", METHOD_NAME, postId);
                return Result.error(404, "指定帖子不存在");
            }

            // 异步更新计数
            CompletableFuture.runAsync(() -> {
                try {
                    postService.incrementViewCount(postId);
                    logger.debug("{} 浏览量更新成功 - postId: {}", METHOD_NAME, postId);
                } catch (Exception e) {
                    logger.error("{} 浏览量更新异常 - postId: {} | 错误: {}", METHOD_NAME, postId, e.getMessage());
                }
            });

            // 构建响应数据
            List<Topic> topics = topicService.getTopicsByPostId(postId);
            PostDetailVO postDetail = new PostDetailVO(post, topics);

            logger.debug("{} 执行成功 - 响应数据: {}", METHOD_NAME, postDetail);
            return Result.success(postDetail);
        } catch (BusinessException be) {
            logger.error("{} 业务异常 - postId: {} | 错误码: {} | 原因: {}", 
                METHOD_NAME, postId, be.getErrorCode(), be.getMessage());
            return Result.error(be.getErrorCode(), be.getMessage());
        } catch (Exception e) {
            logger.error("{} 系统异常 - postId: {}", METHOD_NAME, postId, e);
            return Result.error(500, "服务暂时不可用，请稍后重试");
        } finally {
            logger.debug("{} 方法结束 - postId: {}", METHOD_NAME, postId);
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
            logger.info("收到删除帖子请求，postId: {}", postId);
            
            // 从安全上下文获取当前用户ID
            Long currentUserId = getCurrentUserId();
            logger.info("从安全上下文获取到当前用户ID: {}", currentUserId);
            
            // 获取帖子信息
            Post post = postService.getPostById(postId);
            if (post == null) {
                logger.error("要删除的帖子不存在，postId: {}", postId);
                return Result.error("帖子不存在");
            }
            
            // 权限验证：只能删除自己的帖子
            if (!post.getUserId().equals(currentUserId)) {
                logger.error("用户无权删除此帖子，currentUserId: {}, postUserId: {}", currentUserId, post.getUserId());
                return Result.error("无权删除此帖子");
            }
            
            // 执行删除操作
            boolean success = postService.deletePost(postId);
            if (success) {
                logger.info("帖子删除成功，postId: {}", postId);
                return Result.success(true);
            } else {
                logger.error("帖子删除失败，postId: {}", postId);
                return Result.error("删除帖子失败");
            }
        } catch (BusinessException e) {
            logger.error("删除帖子业务异常，postId: {}", postId, e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除帖子系统异常，postId: {}", postId, e);
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
    @GetMapping("/list")
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date lastTime,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            ScrollResult<PostVO> result = postService.getPostsWaterfall(lastTime, limit);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("瀑布流加载失败", e);
            return Result.error("瀑布流加载失败: " + e.getMessage());
        }
    }
    
    /**
     * 根路径处理，重定向到列表接口
     *
     * @return 重定向到列表接口
     */
    @GetMapping(value = {"/", ""})
    public Result<ScrollResult<PostVO>> getRoot() {
        logger.debug("访问根路径 /api/posts/，重定向到列表接口");
        try {
            return getPostList(null, 10);
        } catch (Exception e) {
            logger.error("根路径处理失败", e);
            return Result.error("获取帖子列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户发布的帖子列表
     *
     * @param page 页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 帖子列表
     */
    @GetMapping("/my")
    public Result<ScrollResult<PostVO>> getMyPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            logger.info("获取当前用户发布的帖子列表，page: {}, pageSize: {}", page, pageSize);
            
            // 从安全上下文获取当前用户ID
            Long userId = getCurrentUserId();
            logger.info("从安全上下文获取到当前用户ID: {}", userId);
            
            ScrollResult<PostVO> result = postService.getPostsByUserId(userId, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取当前用户帖子列表失败", e);
            return Result.error("获取帖子列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取热点帖子列表
     *
     * @param limit 获取条数，默认10条
     * @return 热点帖子列表
     */
    @GetMapping("/hot")
    public Result<List<HotPostVO>> getHotPosts(@RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("获取热点帖子列表，限制条数: {}", limit);
            List<HotPostVO> hotPosts = postService.getHotPosts(limit);
            return Result.success(hotPosts);
        } catch (Exception e) {
            logger.error("获取热点帖子列表失败", e);
            return Result.error("获取热点帖子列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热点帖子列表（带分页）
     *
     * @param page 页码，从1开始
     * @param pageSize 每页大小，默认10
     * @return 热点帖子分页结果
     */
    @GetMapping("/hot/page")
    public Result<ScrollResult<HotPostVO>> getHotPostsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            logger.info("获取热点帖子分页列表，页码: {}, 每页大小: {}", page, pageSize);
            ScrollResult<HotPostVO> result = postService.getHotPostsWithPagination(page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取热点帖子分页列表失败", e);
            return Result.error("获取热点帖子分页列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取突发热点帖子列表
     *
     * @param limit 获取条数，默认5条
     * @return 突发热点帖子列表
     */
    @GetMapping("/hot/burst")
    public Result<List<HotPostVO>> getBurstHotPosts(@RequestParam(defaultValue = "5") int limit) {
        try {
            logger.info("获取突发热点帖子列表，限制条数: {}", limit);
            List<HotPostVO> burstHotPosts = postService.getBurstHotPosts(limit);
            return Result.success(burstHotPosts);
        } catch (Exception e) {
            logger.error("获取突发热点帖子列表失败", e);
            return Result.error("获取突发热点帖子列表失败: " + e.getMessage());
        }
    }
}