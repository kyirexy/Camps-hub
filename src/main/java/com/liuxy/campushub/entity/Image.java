package com.liuxy.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片实体类
 *
 * @author liuxy
 * @since 2024-04-07
 */
@Data
@Entity
@Table(name = "image")
public class Image {
    /**
     * 图片自增唯一ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 存储路径
     */
    @Column(name = "file_path", nullable = false)
    private String filePath;

    /**
     * 原始文件名
     */
    @Column(name = "origin_name", nullable = false, length = 100)
    private String originName;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size", nullable = false)
    private Integer fileSize;

    /**
     * 状态: 1=有效, 0=删除
     */
    @Column(nullable = false)
    private Integer status = 1;

    /**
     * 上传者ID
     */
    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    /**
     * 上传时间
     */
    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    /**
     * 图片用途
     */
    @Column(name = "usage_type", nullable = false, length = 20)
    private String usageType = "general";

    /**
     * 文件类型
     */
    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType;

    /**
     * 文件名
     */
    @Column(name = "filename", nullable = false, length = 100)
    private String filename;

    /**
     * 原始文件名
     */
    @Column(name = "original_filename", nullable = false, length = 100)
    private String originalFilename;

    /**
     * 存储路径
     */
    @Column(name = "stored_path", nullable = false, length = 255)
    private String storedPath;

    /**
     * 图片用途枚举
     */
    public enum UsageType {
        AVATAR("avatar"),
        COVER("cover"),
        GENERAL("general");

        private final String value;

        UsageType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
} 