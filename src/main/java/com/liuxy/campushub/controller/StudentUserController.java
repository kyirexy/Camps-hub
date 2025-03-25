package com.liuxy.campushub.controller;

import com.liuxy.campushub.dto.*;
import com.liuxy.campushub.service.StudentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * 学生用户控制器
 * 
 * 接口文档：
 * 
 * 1. 用户注册
 * POST /api/v1/student/register
 * 请求体：
 * {
 *   "username": "string",     // 用户名，3-20个字符
 *   "password": "string"      // 密码，6-20个字符
 * }
 * 响应：
 * {
 *   "success": boolean,       // 是否成功
 *   "message": "string",      // 响应消息
 *   "userId": number,         // 用户ID（成功时返回）
 *   "username": "string"      // 用户名（成功时返回）
 * }
 * 
 * 2. 用户登录
 * POST /api/v1/student/login
 * 请求体：
 * {
 *   "loginId": "string",      // 登录凭证（用户名/手机号/学号）
 *   "password": "string"      // 密码
 * }
 * 响应：
 * {
 *   "success": boolean,       // 是否成功
 *   "message": "string",      // 响应消息
 *   "userId": number,         // 用户ID（成功时返回）
 *   "username": "string",     // 用户名（成功时返回）
 *   "userRole": number,       // 用户角色（成功时返回）
 *   "token": "string"         // JWT令牌（成功时返回）
 * }
 * 
 * 3. 获取用户信息
 * GET /api/v1/student/{userId}
 * 请求头：
 * Authorization: Bearer {token}
 * 响应：
 * {
 *   "userId": number,         // 用户ID
 *   "username": "string",     // 用户名
 *   "realName": "string",     // 真实姓名
 *   "studentNumber": "string", // 学号
 *   "collegeId": number,      // 院系ID
 *   "major": "string",        // 专业
 *   "grade": number,          // 年级
 *   "phone": "string",        // 手机号
 *   "email": "string",        // 邮箱
 *   "bio": "string",          // 个人简介
 *   "userRole": number,       // 用户角色
 *   "status": number          // 账号状态
 * }
 * 
 * 4. 更新用户信息
 * PUT /api/v1/student/{userId}
 * 请求头：
 * Authorization: Bearer {token}
 * 请求体：
 * {
 *   "realName": "string",     // 真实姓名（可选）
 *   "phone": "string",        // 手机号（可选）
 *   "email": "string",        // 邮箱（可选）
 *   "bio": "string"           // 个人简介（可选）
 * }
 * 响应：
 * {
 *   "success": boolean,       // 是否成功
 *   "message": "string"       // 响应消息
 * }
 * 
 * 5. 修改密码
 * PUT /api/v1/student/{userId}/password
 * 请求头：
 * Authorization: Bearer {token}
 * 请求体：
 * {
 *   "oldPassword": "string",  // 旧密码
 *   "newPassword": "string"   // 新密码
 * }
 * 响应：
 * {
 *   "success": boolean,       // 是否成功
 *   "message": "string"       // 响应消息
 * }
 * 
 * 6. 用户登出
 * POST /api/v1/student/logout
 * 请求头：
 * Authorization: Bearer {token}
 * 响应：
 * 204 No Content
 */
@RestController
@RequestMapping("/api/v1/student")
public class StudentUserController {

    private static final Logger logger = LoggerFactory.getLogger(StudentUserController.class);

    @Autowired
    private StudentUserService studentUserService;

   /* @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        logger.info("Test endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "API is working");
        return ResponseEntity.ok(response);
    }*/

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());
        try {
            logger.debug("Registration request details: {}", request);
            RegisterResponse response = studentUserService.register(request);
            logger.info("Registration completed for username: {}, success: {}", 
                request.getUsername(), response.isSuccess());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for username: " + request.getUsername(), e);
            throw e;
        }
    }

    /**
     * 用户信息查询
     * @param userId 用户ID路径参数
     * @return 用户详细信息
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or principal.userId == #userId")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long userId) {
        logger.info("查询用户信息，用户ID：{}", userId);
        try {
            UserInfoDTO userInfo = studentUserService.getUserInfoById(userId);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            logger.error("查询用户信息失败，用户ID：" + userId, e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        studentUserService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("收到登录请求，登录凭证：{}", request.getLoginId());
        try {
            LoginResponse response = studentUserService.login(request);
            logger.info("用户登录成功：{}", request.getLoginId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("登录失败：" + request.getLoginId(), e);
            throw e;
        }
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or principal.userId == #userId")
    public ResponseEntity<UpdateResponse> updateUserInfo(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        logger.info("更新用户信息，用户ID：{}", userId);
        try {
            UpdateResponse response = studentUserService.updateUserInfo(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("更新用户信息失败，用户ID：" + userId, e);
            throw e;
        }
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 密码修改请求
     * @return 修改结果
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or principal.userId == #userId")
    public ResponseEntity<UpdateResponse> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        logger.info("修改密码，用户ID：{}", userId);
        try {
            UpdateResponse response = studentUserService.changePassword(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("修改密码失败，用户ID：" + userId, e);
            throw e;
        }
    }

}