package com.liuxy.campushub.enums;

public enum BountyStatusEnum {
    CLOSED("closed", "已关闭"),
    OPEN("open", "开放中"),
    PROCESSING("processing", "处理中"),
    COMPLETED("completed", "已完成");

    private final String code;
    private final String description;

    BountyStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static BountyStatusEnum fromCode(String code) {
        for (BountyStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid bounty status code: " + code);
    }
}