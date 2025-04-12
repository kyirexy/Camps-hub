package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Post;
import com.liuxy.campushub.mapper.PostMapper;
import com.liuxy.campushub.service.PostService;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.service.CategoryService;
import com.liuxy.campushub.vo.PostVO;
import com.liuxy.campushub.vo.ScrollResult;
import com.liuxy.campushub.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 帖子服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Value("${file.upload.path}")
    private String uploadPath;

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
            post.setStatus("published");
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setShareCount(0);
            
            // 创建帖子
            int result = postMapper.insert(post);
            if (result <= 0) {
                throw new BusinessException("创建帖子失败");
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
    public Post getPostById(Long postId) {
        return postMapper.selectById(postId);
    }

    public List<Post> getPostsByCategory(Integer categoryId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return postMapper.selectByCategory(categoryId, offset, pageSize);
    }

    public List<Post> getPostsByUser(Long userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return postMapper.selectByUser(userId, offset, pageSize);
    }

    @Override
    @Transactional
    public boolean updatePostStatus(Long postId, String status) {
        Post post = new Post();
        post.setPostId(postId);
        post.setStatus(status);
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
    @Transactional
    public boolean deletePost(Long postId) {
        return postMapper.deleteById(postId) > 0;
    }

    /**
     * 处理图片上传
     */
    private void processImages(Long postId, List<String> base64Images) {
        try {
            for (String base64Image : base64Images) {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                String fileName = UUID.randomUUID().toString() + ".jpg";
                String filePath = uploadPath + File.separator + fileName;
                
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(imageBytes);
                }
                
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
            
            // 多查询一条数据，用于判断是否还有更多
            List<PostVO> posts = postMapper.getPostsWaterfall(lastTime, limit + 1);
            
            ScrollResult<PostVO> result = new ScrollResult<>();
            boolean hasMore = posts.size() > limit;
            
            // 如果有更多数据，移除多查询的一条
            if (hasMore) {
                posts = posts.subList(0, limit);
            }
            
            result.setItems(posts);
            result.setHasMore(hasMore);
            
            // 设置下一次加载的时间戳
            if (!posts.isEmpty()) {
                result.setNextTimestamp(posts.get(posts.size() - 1).getCreatedAt());
            }
            
            logger.info("瀑布流加载完成，返回数据条数: {}, 是否有更多: {}", posts.size(), hasMore);
            return result;
            
        } catch (Exception e) {
            logger.error("瀑布流加载失败", e);
            throw new RuntimeException("瀑布流加载失败: " + e.getMessage());
        }
    }
} 