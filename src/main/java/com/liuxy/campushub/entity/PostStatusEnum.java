package com.liuxy.campushub.entity;

/**
 * 帖子状态枚举
 */
public enum PostStatusEnum {
    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    HIDDEN("hidden", "已隐藏"),
    DELETED("deleted", "已删除");

    private final String code;
    private final String description;

    PostStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PostStatusEnum fromCode(String code) {
        for (PostStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid post status code: " + code);
    }
} 