package com.liuxy.campushub.controller;

import com.liuxy.campushub.entity.Attachment;
import com.liuxy.campushub.service.AttachmentService;
import com.liuxy.campushub.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 附件控制器
 *
 * @author liuxy
 * @since 2024-04-07
 */
@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @param postId 帖子ID
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<Long> uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("postId") Long postId) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件为空");
            }
            Long fileId = attachmentService.uploadFile(file, postId);
            return Result.success(fileId);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @param postId 帖子ID
     * @return 上传结果
     */
    @PostMapping("/batch-upload")
    public Result<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                        @RequestParam("postId") Long postId) {
        try {
            if (files.isEmpty()) {
                return Result.error("文件列表为空");
            }
            List<Long> fileIds = attachmentService.uploadFiles(files, postId);
            return Result.success(fileIds);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取附件信息
     *
     * @param fileId 附件ID
     * @return 附件信息
     */
    @GetMapping("/{fileId}")
    public Result<Attachment> getAttachment(@PathVariable Long fileId) {
        try {
            Attachment attachment = attachmentService.getAttachmentById(fileId);
            if (attachment == null) {
                return Result.error("附件不存在");
            }
            return Result.success(attachment);
        } catch (Exception e) {
            return Result.error("获取附件信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子的所有附件
     *
     * @param postId 帖子ID
     * @return 附件列表
     */
    @GetMapping("/post/{postId}")
    public Result<List<Attachment>> getAttachmentsByPost(@PathVariable Long postId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByPostId(postId);
            return Result.success(attachments);
        } catch (Exception e) {
            return Result.error("获取附件列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除附件
     *
     * @param fileId 附件ID
     * @return 删除结果
     */
    @DeleteMapping("/{fileId}")
    public Result<Boolean> deleteAttachment(@PathVariable Long fileId) {
        try {
            boolean deleted = attachmentService.deleteAttachment(fileId);
            if (!deleted) {
                return Result.error("附件不存在或删除失败");
            }
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("删除附件失败: " + e.getMessage());
        }
    }

    /**
     * 根据类型获取附件列表
     *
     * @param fileType 文件类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 附件列表
     */
    @GetMapping("/type/{fileType}")
    public Result<List<Attachment>> getAttachmentsByType(@PathVariable String fileType,
                                                       @RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByType(fileType, pageNum, pageSize);
            return Result.success(attachments);
        } catch (Exception e) {
            return Result.error("获取附件列表失败: " + e.getMessage());
        }
    }
}