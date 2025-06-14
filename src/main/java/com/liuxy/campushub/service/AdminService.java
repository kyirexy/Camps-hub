package com.liuxy.campushub.service;

import com.github.pagehelper.PageInfo;
import com.liuxy.campushub.dto.AdminLoginRequest;
import com.liuxy.campushub.dto.AdminLoginResponse;
import com.liuxy.campushub.dto.UserStatisticsResponse;
import com.liuxy.campushub.entity.StudentUser;

public interface AdminService {
    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @param ipAddress 客户端IP
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request, String ipAddress);

    /**
     * 管理员登出
     *
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 获取用户统计信息
     *
     * @return 用户统计信息
     */
    UserStatisticsResponse getUserStatistics();
    
    /**
     * 获取用户列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param username 用户名（可选）
     * @param status 状态（可选）
     * @return 分页用户信息
     */
    PageInfo<StudentUser> getUserList(Integer pageNum, Integer pageSize, String username, Integer status);
}