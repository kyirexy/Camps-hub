package com.liuxy.campushub.enums;

public enum PostTypeEnum {
    NORMAL("normal", "普通帖子"),
    BOUNTY("bounty", "悬赏帖子");

    private final String code;
    private final String description;

    PostTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PostTypeEnum fromCode(String code) {
        for (PostTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid post type code: " + code);
    }
}