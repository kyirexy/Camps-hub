package com.liuxy.campushub.service;

import com.liuxy.campushub.dto.LoginRequest;
import com.liuxy.campushub.dto.LoginResponse;
import com.liuxy.campushub.dto.RegisterRequest;
import com.liuxy.campushub.dto.RegisterResponse;
import com.liuxy.campushub.dto.UserInfoDTO;

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
}