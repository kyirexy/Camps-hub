package com.liuxy.campushub.controller;

import com.github.pagehelper.PageInfo;
import com.liuxy.campushub.dto.AdminLoginRequest;
import com.liuxy.campushub.dto.AdminLoginResponse;
import com.liuxy.campushub.dto.UserStatisticsResponse;
import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 *
 * @author liuxy
 * @date 2024-03-25
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @param httpRequest HTTP请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request,
                                 HttpServletRequest httpRequest) {
        try {
            AdminLoginResponse response = adminService.login(request, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("管理员登录失败", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 管理员登出
     *
     * @param request HTTP请求
     * @return 登出响应
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token != null) {
                adminService.logout(token);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("管理员登出失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 从请求头中提取token
     *
     * @param request HTTP请求
     * @return token字符串
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取用户统计信息
     *
     * @return 用户统计信息
     */
    @GetMapping("/statistics/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
        try {
            UserStatisticsResponse statistics = adminService.getUserStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("获取用户统计信息失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页用户信息
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageInfo<StudentUser>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        try {
            PageInfo<StudentUser> users = adminService.getUserList(pageNum, pageSize, username, status);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("获取用户列表失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}