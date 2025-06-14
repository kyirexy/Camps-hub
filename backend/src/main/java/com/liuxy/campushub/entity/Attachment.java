package com.liuxy.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 附件实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
@Entity
@Table(name = "attachment")
public class Attachment {
    /**
     * 文件唯一ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    /**
     * 关联帖子ID
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 文件类型: image图片/pdf文档/ppt幻灯片/video视频
     */
    private String fileType;

    /**
     * 文件OSS访问地址
     */
    private String fileUrl;

    /**
     * 缩略图地址（图片/视频需要）
     */
    private String thumbnailUrl;

    /**
     * 文件大小（单位KB）
     */
    private Integer fileSize;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    public void setPostId(Long postId) {
        //return this.post.getPostId();
    }
}