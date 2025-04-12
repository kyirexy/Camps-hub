package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 附件服务接口
 *
 * @author liuxy
 * @since 2024-04-07
 */
public interface AttachmentService {
    
    /**
     * 创建附件记录
     *
     * @param postId 帖子ID
     * @param fileName 文件名
     * @return 附件ID
     */
    Long createAttachment(Long postId, String fileName);
    
    /**
     * 上传单个文件
     *
     * @param file 文件
     * @param postId 帖子ID
     * @return 附件ID
     * @throws IOException 如果文件上传失败
     */
    Long uploadFile(MultipartFile file, Long postId) throws IOException;
    
    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @param postId 帖子ID
     * @return 附件ID列表
     * @throws IOException 如果文件上传失败
     */
    List<Long> uploadFiles(List<MultipartFile> files, Long postId) throws IOException;
    
    /**
     * 根据ID查询附件
     *
     * @param fileId 附件ID
     * @return 附件实体
     */
    Attachment getAttachmentById(Long fileId);
    
    /**
     * 根据帖子ID查询附件列表
     *
     * @param postId 帖子ID
     * @return 附件列表
     */
    List<Attachment> getAttachmentsByPostId(Long postId);
    
    /**
     * 根据文件类型查询附件
     *
     * @param fileType 文件类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 附件列表
     */
    List<Attachment> getAttachmentsByType(String fileType, int pageNum, int pageSize);
    
    /**
     * 删除附件
     *
     * @param fileId 附件ID
     * @return 是否删除成功
     */
    boolean deleteAttachment(Long fileId);
    
    /**
     * 删除帖子相关的所有附件
     *
     * @param postId 帖子ID
     * @return 是否删除成功
     */
    boolean deleteAttachmentsByPostId(Long postId);
    
    /**
     * 生成文件缩略图
     *
     * @param file 文件
     * @return 缩略图URL
     */
    String generateThumbnail(File file);
    
    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    String getFileUrl(Long fileId);
    
    /**
     * 获取文件缩略图URL
     *
     * @param fileId 文件ID
     * @return 缩略图URL
     */
    String getThumbnailUrl(Long fileId);
}