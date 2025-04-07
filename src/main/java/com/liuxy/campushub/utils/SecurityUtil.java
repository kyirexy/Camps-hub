package com.liuxy.campushub.utils;

import com.liuxy.campushub.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 */
public class SecurityUtil {
    
    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
} 