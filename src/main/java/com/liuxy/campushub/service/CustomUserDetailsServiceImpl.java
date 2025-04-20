package com.liuxy.campushub.service;

import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.entity.Admin;
import com.liuxy.campushub.mapper.StudentUserMapper;
import com.liuxy.campushub.mapper.AdminMapper;
import com.liuxy.campushub.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentUserMapper studentUserMapper;
    
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先查询学生用户表
        StudentUser studentUser = studentUserMapper.findByUsername(username);
        if (studentUser != null) {
            String role = "USER";
            if (studentUser.getUserRole() == 1) {
                role = "ADMIN";
            }
            return UserDetailsImpl.build(
                studentUser.getUserId(),
                studentUser.getUsername(),
                studentUser.getPassword(),
                studentUser.getEmail(),
                Arrays.asList(role)
            );
        }
        
        // 如果学生用户表找不到，再查询管理员表
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null) {
            return UserDetailsImpl.build(
                admin.getId().longValue(),
                admin.getUsername(),
                admin.getPasswordHash(),
                null,
                Arrays.asList("ADMIN")
            );
        }
        
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}