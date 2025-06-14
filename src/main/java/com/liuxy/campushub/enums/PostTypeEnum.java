package com.liuxy.campushub.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum PostTypeEnum implements IEnum<String> {
    NORMAL("normal"),
    BOUNTY("bounty");

    private final String value;

    PostTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static PostTypeEnum fromValue(String value) {
        for (PostTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PostTypeEnum value: " + value);
    }
}