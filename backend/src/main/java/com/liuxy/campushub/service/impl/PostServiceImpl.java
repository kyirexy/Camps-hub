package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.*;
import com.liuxy.campushub.enums.BountyStatusEnum;
import com.liuxy.campushub.enums.PostTypeEnum;
import com.liuxy.campushub.entity.PostStatusEnum;
import com.liuxy.campushub.mapper.PostMapper;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.service.CategoryService;
import com.liuxy.campushub.service.TopicService;
import com.liuxy.campushub.vo.PostDetailVO;
import com.liuxy.campushub.vo.PostVO;
import com.liuxy.campushub.vo.ScrollResult;
import com.liuxy.campushub.vo.HotPostVO;
import com.liuxy.campushub.model.HotPostModel;
import com.liuxy.campushub.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.io.FileInputStream;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import com.liuxy.campushub.util.SftpUtil;
import java.time.ZoneId;
import java.time.Instant;
import com.liuxy.campushub.vo.PostDetailResponseVO;

/**
 * 帖子服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final AttachmentService attachmentService;
    private final CategoryService categoryService;
    private final TopicService topicService;
    private final SftpUtil sftpUtil;

    // 注入图片访问基础URL
    @Value("${image.access.base-url:http://localhost:8081}") 
    private String imageBaseUrl;

    // 注入头像图片相对路径
    @Value("${image.access.path.avatars:/avatars}") 
    private String avatarPath;

    @Autowired
    public PostServiceImpl(PostMapper postMapper, 
                          AttachmentService attachmentService,
                          CategoryService categoryService,
                          TopicService topicService,
                          SftpUtil sftpUtil) {
        this.postMapper = postMapper;
        this.attachmentService = attachmentService;
        this.categoryService = categoryService;
        this.topicService = topicService;
        this.sftpUtil = sftpUtil;
    }

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.avatar.path}")
    private String avatarDiskPath; // 修改字段名避免冲突

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
    @Transactional(rollbackFor = Exception.class)
    public Long createPost(Post post, List<String> base64Images) {
        try {
            logger.info("开始创建帖子，Post对象: {}", post);
            
            // 参数校验
            if (post.getCategoryId() == null) {
                throw new BusinessException("分类ID不能为空");
            }
            if (post.getUserId() == null) {
                throw new BusinessException("用户ID不能为空");
            }
            
            // 验证分类是否存在
            if (categoryService.getCategoryById(post.getCategoryId()) == null) {
                throw new BusinessException("分类不存在，categoryId: " + post.getCategoryId());
            }
            
            // 设置默认值
            post.setStatus(PostStatusEnum.PUBLISHED);
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setShareCount(0);
            
            // 根据帖子类型设置悬赏相关字段
            if (post.getPostType() == PostTypeEnum.BOUNTY) {
                post.setBountyAmount(BigDecimal.ZERO);
                post.setBountyStatus(BountyStatusEnum.OPEN);
                post.setEmergencyLevel(0);
            } else {
                // 普通帖子不需要设置悬赏相关字段
                post.setBountyAmount(null);
                post.setBountyStatus(null);
                post.setEmergencyLevel(null);
            }
            
            // 创建帖子
            int result = postMapper.insert(post);
            if (result <= 0) {
                throw new BusinessException("创建帖子失败");
            }
            
            // 处理话题关联
            if (post.getTopics() != null && !post.getTopics().isEmpty()) {
                topicService.batchLinkPostTopic(post.getPostId(), 
                    post.getTopics().stream().map(Topic::getTopicId).collect(Collectors.toList()));
            }
            
            // 处理图片
            if (!CollectionUtils.isEmpty(base64Images)) {
                processImages(post.getPostId(), base64Images);
            }
            
            return post.getPostId();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("创建帖子失败", e);
            throw new BusinessException("创建帖子失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(Post post, List<String> base64Images) {
        try {
            logger.info("开始更新帖子，postId: {}", post.getPostId());
            
            // 参数校验
            if (post.getPostId() == null) {
                throw new BusinessException("帖子ID不能为空");
            }
            
            // 更新帖子
            boolean updated = postMapper.updateById(post) > 0;
        // 更新话题关联
        postMapper.deletePostTopicsByPostId(post.getPostId());
        if (post.getTopics() != null && !post.getTopics().isEmpty()) {
            postMapper.insertPostTopics(post.getPostId(),
                post.getTopics().stream().map(Topic::getTopicId).collect(Collectors.toList()));
        };
            if (!updated) {
                throw new BusinessException("更新帖子失败");
            }
            
            // 处理图片
            if (!CollectionUtils.isEmpty(base64Images)) {
                // 删除旧的附件
                attachmentService.deleteAttachmentsByPostId(post.getPostId());
                // 添加新的附件
                processImages(post.getPostId(), base64Images);
            }
            
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新帖子失败", e);
            throw new BusinessException("更新帖子失败: " + e.getMessage());
        }
    }

    @Override
    public PostDetailResponseVO getPostDetail(Long postId) {
        try {
            logger.info("开始获取帖子详情，postId: {}", postId);
            
            // 获取帖子基本信息
            Post post = getPostById(postId);
            if (post == null) {
                throw new BusinessException(404, "帖子不存在");
            }
            
            // 获取帖子关联的话题
            List<Topic> topics = topicService.getTopicsByPostId(postId);
            
            // 构建并返回详情VO，在构建时处理URL拼接
            PostDetailResponseVO detailVO = PostDetailResponseVO.fromEntities(post, topics, imageBaseUrl, avatarPath);

            logger.info("获取帖子详情成功，postId: {}", postId);
            return detailVO;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取帖子详情失败", e);
            throw new BusinessException(500, "获取帖子详情失败: " + e.getMessage());
        }
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            logger.error("帖子不存在，postId: {}", postId);
            throw new BusinessException("帖子不存在");
        }
        // 获取关联话题
        List<Topic> topics = topicService.getTopicsByPostId(postId);
        post.setTopics(new ArrayList<>(topics));
        
        // 获取附件
        /*List<Attachment> attachments = attachmentService.getAttachmentsByPostId(postId);
        post.setAttachments(new ArrayList<>(attachments));*/
        
        return post;
    }

    

    @Override
    @Transactional
    public boolean updatePostStatus(Long postId, String status) {
        Post post = new Post();
        post.setPostId(postId);
        post.setStatus(PostStatusEnum.fromCode(status));
        return postMapper.updateById(post) > 0;
    }

    @Override
    @Transactional
    public boolean incrementViewCount(Long postId) {
        return postMapper.incrementViewCount(postId) > 0;
    }

    @Override
    @Transactional
    public boolean incrementLikeCount(Long postId) {
        return postMapper.incrementLikeCount(postId) > 0;
    }

    @Override
    @Transactional
    public boolean incrementCommentCount(Long postId) {
        return postMapper.incrementCommentCount(postId) > 0;
    }

    @Override
    @Transactional
    public boolean incrementShareCount(Long postId) {
        return postMapper.incrementShareCount(postId) > 0;
    }

    @Override
    public ScrollResult<PostVO> getPostList(Date lastTime, int pageSize) {
        try {
            logger.info("开始获取帖子列表，lastTime: {}, pageSize: {}", lastTime, pageSize);
            
            // 多查询一条数据，用于判断是否还有更多数据
            List<PostVO> posts = postMapper.selectPostsByTime(lastTime, pageSize + 1);
            
            // 处理分页结果
            ScrollResult<PostVO> result = new ScrollResult<>();
            boolean hasMore = posts.size() > pageSize;
            
            // 如果有更多数据，则移除多查询的一条
            if (hasMore) {
                posts = posts.subList(0, pageSize);
            }
            
            result.setItems(posts);
            result.setHasMore(hasMore);
            
            // 设置下一页的时间戳
            if (!posts.isEmpty()) {
                result.setNextTimestamp(posts.get(posts.size() - 1).getCreatedAt());
            }
            
            logger.info("获取帖子列表完成，返回数据条数: {}, 是否有更多: {}", posts.size(), hasMore);
            return result;
            
        } catch (Exception e) {
            logger.error("获取帖子列表失败", e);
            throw new RuntimeException("获取帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    public ScrollResult<PostVO> getPostsByCategory(Integer categoryId, Date lastTime, int pageSize) {
        try {
            logger.info("开始获取分类帖子列表，categoryId: {}, lastTime: {}, pageSize: {}", categoryId, lastTime, pageSize);
            
            Map<String, Object> params = new HashMap<>();
            params.put("categoryId", categoryId);
            params.put("pageSize", pageSize + 1);
            if (lastTime != null) {
                params.put("lastTime", lastTime);
            }

            List<PostVO> posts = postMapper.selectByCategoryForScroll(params);
            return processScrollResult(posts, pageSize);
        } catch (Exception e) {
            logger.error("获取分类帖子列表失败", e);
            throw new BusinessException("获取分类帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    public ScrollResult<PostVO> getPostsByUser(Long userId, Date lastTime, int pageSize) {
        try {
            logger.info("开始获取用户帖子列表，userId: {}, lastTime: {}, pageSize: {}", userId, lastTime, pageSize);
            
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("pageSize", pageSize + 1);
            if (lastTime != null) {
                params.put("lastTime", lastTime);
            }

            List<PostVO> posts = postMapper.selectByUserForScroll(params);
            return processScrollResult(posts, pageSize);
        } catch (Exception e) {
            logger.error("获取用户帖子列表失败", e);
            throw new BusinessException("获取用户帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(Long postId) {
        try {
            logger.info("开始删除帖子，postId: {}", postId);
            
            // 1. 删除帖子关联的话题
            topicService.unlinkAllPostTopics(postId);
            logger.info("已删除帖子关联的话题，postId: {}", postId);
            
            // 2. 更新帖子状态为已删除
            Post post = new Post();
            post.setPostId(postId);
            post.setStatus(PostStatusEnum.DELETED);
            post.setUpdatedAt(new Date());
            
            int result = postMapper.updateById(post);
            logger.info("更新帖子状态完成，postId: {}, 影响行数: {}", postId, result);
            
            return result > 0;
        } catch (Exception e) {
            logger.error("删除帖子失败，postId: {}", postId, e);
            throw new BusinessException("删除帖子失败: " + e.getMessage());
        }
    }

    /**
     * 处理图片上传
     */
    private void processImages(Long postId, List<String> base64Images) {
        try {
            for (String base64Image : base64Images) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                String fileName = UUID.randomUUID().toString() + ".jpg";
                
                // 创建临时文件
                File tempFile = File.createTempFile("upload_", ".jpg");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(imageBytes);
                }
                
                // 上传到SFTP服务器
                try (FileInputStream fis = new FileInputStream(tempFile)) {
                    MultipartFile multipartFile = new MockMultipartFile(
                        fileName,
                        fileName,
                        "image/jpeg",
                        fis
                    );
                    sftpUtil.uploadFile(multipartFile, avatarDiskPath);
                }
                
                // 删除临时文件
                tempFile.delete();
                
                // 创建附件记录
                attachmentService.createAttachment(postId, fileName);
            }
        } catch (Exception e) {
            logger.error("处理图片失败", e);
            throw new BusinessException("处理图片失败: " + e.getMessage());
        }
    }

    /**
     * 处理滚动分页结果
     */
    private ScrollResult<PostVO> processScrollResult(List<PostVO> posts, int pageSize) {
        boolean hasMore = posts.size() > pageSize;
        if (hasMore) {
            posts = posts.subList(0, pageSize);
        }
        
        ScrollResult<PostVO> result = new ScrollResult<>();
        result.setItems(posts);
        result.setHasMore(hasMore);
        if (!posts.isEmpty()) {
            result.setNextTimestamp(posts.get(posts.size()-1).getCreatedAt());
        }
        return result;
    }

    @Override
    public ScrollResult<PostVO> getPostsWaterfall(Date lastTime, int limit) {
        try {
            logger.info("开始瀑布流加载帖子列表，lastTime: {}, limit: {}", lastTime, limit);
            
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("status", PostStatusEnum.PUBLISHED.getCode());
            params.put("limit", limit);
            
            // 如果有lastTime参数，则添加时间条件
            if (lastTime != null) {
                params.put("lastTime", lastTime);
            }
            
            // 调用Mapper方法获取帖子列表
            List<PostVO> posts = postMapper.getPostsWaterfall(params);
            logger.info("成功加载 {} 条帖子", posts.size());
            
            // 拼接完整的头像URL
            if (posts != null && !posts.isEmpty()) {
                for (PostVO post : posts) {
                    if (post.getAvatar() != null && !post.getAvatar().isEmpty()) {
                        post.setAvatar(imageBaseUrl + avatarPath + "/" + post.getAvatar());
                    }
                }
            }
            
            // 构建返回结果
            ScrollResult<PostVO> result = new ScrollResult<>();
            result.setItems(posts);
            result.setHasMore(!posts.isEmpty());
            if (!posts.isEmpty()) {
                result.setNextTimestamp(posts.get(posts.size() - 1).getCreatedAt());
            }
            return result;
        } catch (Exception e) {
            logger.error("瀑布流加载失败", e);
            throw new BusinessException("加载帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    public ScrollResult<PostVO> getPostsByUserId(Long userId, int page, int pageSize) {
        try {
            logger.info("开始获取用户帖子列表，userId: {}, page: {}, pageSize: {}", userId, page, pageSize);
            
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("offset", (page - 1) * pageSize);
            params.put("pageSize", pageSize);

            List<PostVO> posts = postMapper.selectByUser(params);
            return processScrollResult(posts, pageSize);
        } catch (Exception e) {
            logger.error("获取用户帖子列表失败", e);
            throw new BusinessException("获取用户帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    public List<HotPostVO> getHotPosts(int limit) {
        try {
            logger.info("开始获取热点帖子列表，limit: {}", limit);
        
            // 获取热点帖子列表
            List<PostVO> postVOs = postMapper.getHotPosts(limit);
            if (CollectionUtils.isEmpty(postVOs)) {
            return Collections.emptyList();
        }
        
            // 转换为HotPostVO
            return postVOs.stream()
                    .map(postVO -> {
                        HotPostVO hotPostVO = new HotPostVO();
                        BeanUtils.copyProperties(postVO, hotPostVO);
                        
                        // 计算热度值
                        double hotness = calculateHotness(postVO);
                        hotPostVO.setHotness(hotness);
                        
                        // Date -> LocalDateTime
                        Date createdAt = postVO.getCreatedAt();
                        LocalDateTime ldt = LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault());
                        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
                        hotPostVO.setIsNew(ldt.isAfter(oneDayAgo));
                        
                        // 判断是否为突发热点
                        hotPostVO.setIsBurst(postVO.getCommentCount() > 100);
                        
                        return hotPostVO;
                    })
                    .sorted(Comparator.comparing(HotPostVO::getHotness).reversed())
                .collect(Collectors.toList());
        
        } catch (Exception e) {
            logger.error("获取热点帖子列表失败", e);
            throw new BusinessException("获取热点帖子列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算帖子热度值
     * 
     * @param postVO 帖子VO对象
     * @return 热度值
     */
    private double calculateHotness(PostVO postVO) {
        // 获取时间衰减因子
        Date createdAt = postVO.getCreatedAt();
        LocalDateTime ldt = LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault());
        long hoursSinceCreation = ChronoUnit.HOURS.between(ldt, LocalDateTime.now());
        double timeDecay = Math.pow(0.2, hoursSinceCreation);
        
        // 计算基础热度值
        double baseHotness = postVO.getViewCount() * 0.2 +
                           postVO.getLikeCount() * 0.3 +
                           postVO.getCommentCount() * 0.25 +
                           postVO.getShareCount() * 0.1;
        
        // 应用时间衰减
        return baseHotness * timeDecay;
    }
    
    @Override
    public ScrollResult<HotPostVO> getHotPostsWithPagination(int page, int pageSize) {
        logger.info("获取热点帖子分页列表，页码: {}, 每页大小: {}", page, pageSize);
        
        // 获取所有已发布的帖子
        List<Post> posts = postMapper.findAllByStatus(PostStatusEnum.PUBLISHED);
        if (CollectionUtils.isEmpty(posts)) {
            logger.info("没有找到已发布的帖子");
            return new ScrollResult<>();
        }
        
        // 计算热度并排序
        List<Post> sortedPosts = posts.stream()
                .map(post -> {
                    double hotness = HotPostModel.calculateHotness(post);
                    post.setHotness(hotness);
                    return post;
                })
                .sorted((p1, p2) -> Double.compare(p2.getHotness(), p1.getHotness()))
                .collect(Collectors.toList());
        
        // 分页处理
        int totalSize = sortedPosts.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalSize);
        
        if (startIndex >= totalSize) {
            logger.info("页码超出范围，总记录数: {}, 请求页码: {}", totalSize, page);
            return new ScrollResult<>();
        }
        
        List<Post> pagePosts = sortedPosts.subList(startIndex, endIndex);
        
        // 转换为HotPostVO
        List<HotPostVO> hotPostVOs = convertToHotPostVO(pagePosts);
        
        // 设置排名
        for (int i = 0; i < hotPostVOs.size(); i++) {
            hotPostVOs.get(i).setRank(startIndex + i + 1);
        }
        
        // 构建分页结果
        ScrollResult<HotPostVO> result = new ScrollResult<>();
        result.setItems(hotPostVOs);
        result.setHasMore(endIndex < totalSize);
        
        // 设置下次请求的时间参数（这里使用当前时间，因为热点排序不依赖时间）
        result.setNextTimestamp(new Date());
        
        return result;
    }
    
    @Override
    public List<HotPostVO> getBurstHotPosts(int limit) {
        logger.info("获取突发热点帖子列表，限制条数: {}", limit);
        
        // 获取所有已发布的帖子
        List<Post> posts = postMapper.findAllByStatus(PostStatusEnum.PUBLISHED);
        if (CollectionUtils.isEmpty(posts)) {
            logger.info("没有找到已发布的帖子");
            return Collections.emptyList();
        }
        
        // 筛选突发热点（评论量超过阈值且24小时内发布）
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<Post> burstPosts = posts.stream()
                .filter(post -> post.getCommentCount() > 100 && 
                        post.getCreatedAt().isAfter(oneDayAgo))
                .map(post -> {
                    double hotness = HotPostModel.calculateHotness(post);
                    post.setHotness(hotness);
                    return post;
                })
                .sorted((p1, p2) -> Double.compare(p2.getHotness(), p1.getHotness()))
                .limit(limit)
                .collect(Collectors.toList());
        
        // 转换为HotPostVO
        List<HotPostVO> hotPostVOs = convertToHotPostVO(burstPosts);
        
        // 设置排名和突发热点标记
        for (int i = 0; i < hotPostVOs.size(); i++) {
            hotPostVOs.get(i).setRank(i + 1);
            hotPostVOs.get(i).setIsBurst(true);
        }
        
        return hotPostVOs;
    }
    
    /**
     * 将Post实体转换为HotPostVO
     * 
     * @param posts 帖子列表
     * @return 热点帖子VO列表
     */
    private List<HotPostVO> convertToHotPostVO(List<Post> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }
        
        // 获取所有帖子ID
        List<Long> postIds = posts.stream()
                .map(Post::getPostId)
                .collect(Collectors.toList());
        
        // 批量获取帖子VO (Mapper查询会包含avatar文件名)
        List<PostVO> postVOs = postMapper.findPostVOsByIds(postIds);
        
        // 将PostVO转换为HotPostVO并拼接头像URL
        return postVOs.stream()
                .map(postVO -> {
                    HotPostVO hotPostVO = new HotPostVO();
                    // 复制PostVO属性，包括avatar文件名
                    BeanUtils.copyProperties(postVO, hotPostVO);
                    
                    // 拼接完整的头像URL
                    if (hotPostVO.getAvatar() != null) {
                        hotPostVO.setAvatar(getCompleteAvatarUrl(hotPostVO.getAvatar()));
                    }
                    
                    // 查找对应的Post实体，用于设置热度、isNew、isBurst等字段
                    Post post = posts.stream()
                            .filter(p -> p.getPostId().equals(postVO.getPostId()))
                            .findFirst()
                            .orElse(null);
                    
                    if (post != null) {
                        // 设置热度值
                        hotPostVO.setHotness(post.getHotness());
                        
                        // 判断是否为新发布（24小时内）
                        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
                        hotPostVO.setIsNew(post.getCreatedAt().isAfter(oneDayAgo));
                        
                        // 判断是否为突发热点
                        hotPostVO.setIsBurst(post.getCommentCount() > 100);
                    }
                    
                    return hotPostVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> getLatestPosts(int limit) {
        // 获取5天内的已发布帖子，按热度分数降序排列
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        // 获取包含用户头像信息的帖子列表
        List<PostVO> postVOs = postMapper.getLatestHotPosts(fiveDaysAgo, limit);
        
        // 转换为Post实体
        return postVOs.stream()
                .map(postVO -> {
                    Post post = new Post();
                    BeanUtils.copyProperties(postVO, post);
                    return post;
                })
                .collect(Collectors.toList());
    }
}