package com.liuxy.campushub.aidto;

import lombok.Data;

/**
 * API通用响应消息类
 * 用于封装讯飞开放平台API的基础响应信息
 *
 * @author ydwang16
 * @version 2023/09/06 14:11
 **/
@Data
public class ResponseMsg {
    /**
     * 操作标志，表示请求是否成功
     */
    private boolean flag;
    
    /**
     * 响应状态码，0表示成功，其他值表示失败
     */
    private int code;
    
    /**
     * 响应描述信息
     */
    private String desc;
    
    /**
     * 会话ID，用于请求追踪
     */
    private String sid;

    /**
     * 判断请求是否成功
     * @return true表示成功（code=0），false表示失败
     */
    public boolean success() {
        return code == 0;
    }
}