package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.AdminLoginRequest;
import com.liuxy.campushub.dto.AdminLoginResponse;

public interface AdminService {
    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @param ipAddress 客户端IP
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request, String ipAddress);
} 