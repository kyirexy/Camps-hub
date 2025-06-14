package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.entity.Attachment;
import com.liuxy.campushub.mapper.AttachmentMapper;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.util.SftpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 附件服务实现类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private SftpUtil sftpUtil;

    @Value("${sftp.remote-path}")
    private String remotePath;

    @Value("${upload.avatar.url}")
    private String urlPrefix;

    @Value("${upload.avatar.path}")
    private String avatarPath;

    @Override
    @Transactional
    public Long uploadFile(MultipartFile file, Long postId) throws IOException {
        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        // 保存文件到SFTP服务器
        String fileName = sftpUtil.uploadFile(file, avatarPath);
        
        // 创建附件记录
        return createAttachment(postId, fileName);
    }

    @Override
    @Transactional
    public List<Long> uploadFiles(List<MultipartFile> files, Long postId) throws IOException {
        List<Long> fileIds = new ArrayList<>();
        for (MultipartFile file : files) {
            Long fileId = uploadFile(file, postId);
            fileIds.add(fileId);
        }
        return fileIds;
    }

    @Override
    @Transactional
    public Long createAttachment(Long postId, String fileName) {
        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        Attachment attachment = new Attachment();
        attachment.setPostId(postId);
        attachment.setFileType(getFileType(fileName));
        attachment.setFileUrl(urlPrefix + "/" + fileName);
        
        // 设置文件大小
        attachment.setFileSize(0); // 由于使用SFTP，暂时无法获取文件大小

        attachmentMapper.insert(attachment);
        return attachment.getFileId();
    }

    @Override
    public String generateThumbnail(File file) {
        // 由于使用SFTP，暂时不生成缩略图
        return null;
    }

    private String getFileType(String fileName) {
        String extension = getFileExtension(fileName);
        if (extension == null) return "unknown";
        
        if (extension.matches("(?i)jpg|jpeg|png|gif|bmp")) {
            return "image";
        } else if (extension.matches("(?i)mp4|avi|mov|wmv")) {
            return "video";
        } else if (extension.matches("(?i)mp3|wav|ogg")) {
            return "audio";
        } else if (extension.matches("(?i)doc|docx|pdf|txt")) {
            return "document";
        } else {
            return "other";
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return null;
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : null;
    }

    @Override
    @Transactional
    public boolean deleteAttachment(Long fileId) throws IOException {
        Attachment attachment = attachmentMapper.selectById(fileId);
        if (attachment != null) {
            String fileName = attachment.getFileUrl().substring(attachment.getFileUrl().lastIndexOf("/") + 1);
            sftpUtil.deleteFile(avatarPath, fileName);
            return attachmentMapper.deleteById(fileId) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteAttachmentsByPostId(Long postId) throws IOException {
        List<Attachment> attachments = attachmentMapper.selectByPostId(postId);
        for (Attachment attachment : attachments) {
            String fileName = attachment.getFileUrl().substring(attachment.getFileUrl().lastIndexOf("/") + 1);
            sftpUtil.deleteFile(avatarPath, fileName);
        }
        return attachmentMapper.deleteByPostId(postId) > 0;
    }

    @Override
    public Attachment getAttachmentById(Long fileId) {
        return attachmentMapper.selectById(fileId);
    }

    @Override
    public List<Attachment> getAttachmentsByPostId(Long postId) {
        return attachmentMapper.selectByPostId(postId);
    }

    @Override
    public List<Attachment> getAttachmentsByType(String fileType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return attachmentMapper.selectByType(fileType, offset, pageSize);
    }

    @Override
    public String getFileUrl(Long fileId) {
        Attachment attachment = attachmentMapper.selectById(fileId);
        return attachment != null ? attachment.getFileUrl() : null;
    }

    @Override
    public String getThumbnailUrl(Long fileId) {
        Attachment attachment = attachmentMapper.selectById(fileId);
        return attachment != null ? attachment.getThumbnailUrl() : null;
    }
}