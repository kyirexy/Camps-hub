package com.liuxy.campushub.enums;

/**
 * 评论状态枚举
 *
 * @author liuxy
 * @since 2024-04-21
 */
public enum CommentStatusEnum {
    NORMAL("normal"),
    DELETED("deleted");

    private final String code;

    CommentStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CommentStatusEnum fromCode(String code) {
        for (CommentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid comment status code: " + code);
    }
} 