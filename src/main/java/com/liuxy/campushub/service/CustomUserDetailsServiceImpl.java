package com.liuxy.campushub.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 实现从数据库查询用户信息的逻辑
        // 示例硬编码用户，实际应从数据库获取
        return User.builder()
                .username(username)
                .password("$2a$10$5vYQ0sMzjB6UqZ6t7Qq1Uu0ZQzX7bWY8l6JcYtK1d3V7gZ8rL9Df2")
                .roles("USER")
                .build();
    }
}