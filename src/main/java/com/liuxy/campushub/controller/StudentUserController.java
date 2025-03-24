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

}