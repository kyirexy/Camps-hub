package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.dto.AdminLoginRequest;
import com.liuxy.campushub.dto.AdminLoginResponse;
import com.liuxy.campushub.dto.AdminInfoDTO;
import com.liuxy.campushub.entity.Admin;
import com.liuxy.campushub.entity.AdminLog;
import com.liuxy.campushub.mapper.AdminMapper;
import com.liuxy.campushub.mapper.AdminLogMapper;
import com.liuxy.campushub.service.AdminService;
import com.liuxy.campushub.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final AdminLogMapper adminLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginResponse login(AdminLoginRequest request, String clientIp) {
        // 查找管理员
        Admin admin = adminMapper.findByUsername(request.getUsername());
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查状态
        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新登录信息
        LocalDateTime now = LocalDateTime.now();
        adminMapper.updateLoginInfo(admin.getId(), now, clientIp);

        // 记录登录日志
        AdminLog log = new AdminLog();
        log.setAdminId(admin.getId());
        log.setAction("LOGIN");
        log.setTarget("系统");
        log.setIp(clientIp);
        log.setDetail("管理员登录成功");
        adminLogMapper.insert(log);

        // 生成token
        String token = jwtTokenUtil.generateToken(admin.getId().longValue(), admin.getUsername(), 1);

        // 构建响应
        AdminInfoDTO adminInfo = AdminInfoDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .isSuper(admin.getIsSuper())
                .lastLogin(admin.getLastLogin())
                .loginIp(admin.getLoginIp())
                .build();

        return AdminLoginResponse.builder()
                .token(token)
                .admin(adminInfo)
                .build();
    }
} 