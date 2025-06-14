package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.LoginRequest;
import com.liuxy.campushub.dto.LoginResponse;
import com.liuxy.campushub.dto.RegisterRequest;
import com.liuxy.campushub.dto.RegisterResponse;
import com.liuxy.campushub.dto.UserInfoDTO;
import com.liuxy.campushub.dto.UpdateResponse;
import com.liuxy.campushub.dto.UpdateUserRequest;
import com.liuxy.campushub.dto.ChangePasswordRequest;
import org.springframework.web.multipart.MultipartFile;

public interface StudentUserService {
    RegisterResponse register(RegisterRequest request);

    /**
     * 学生用户登录
     * @param request 登录请求参数
     * @return 登录响应结果
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     * @param token 需要失效的JWT令牌
     */
    void logout(String token);

    UserInfoDTO getUserInfoById(Long userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    UpdateResponse updateUserInfo(Long userId, UpdateUserRequest request);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 密码修改请求
     * @return 修改结果
     */
    UpdateResponse changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 上传结果
     */
    UpdateResponse uploadAvatar(Long userId, MultipartFile file);
}