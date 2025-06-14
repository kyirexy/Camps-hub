package com.liuxy.campushub.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liuxy.campushub.dto.AdminLoginRequest;
import com.liuxy.campushub.dto.AdminLoginResponse;
import com.liuxy.campushub.dto.AdminInfoDTO;
import com.liuxy.campushub.dto.UserStatisticsResponse;
import com.liuxy.campushub.entity.Admin;
import com.liuxy.campushub.entity.AdminLog;
import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.mapper.AdminMapper;
import com.liuxy.campushub.mapper.AdminLogMapper;
import com.liuxy.campushub.mapper.StudentUserMapper;
import com.liuxy.campushub.service.AdminService;
import com.liuxy.campushub.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final AdminLogMapper adminLogMapper;
    private final StudentUserMapper studentUserMapper;
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

    @Override
    public void logout(String token) {
        // 记录登出日志
        String username = jwtTokenUtil.parseToken(token).get("username", String.class);
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null) {
            AdminLog log = new AdminLog();
            log.setAdminId(admin.getId());
            log.setAction("LOGOUT");
            log.setTarget("系统");
            log.setDetail("管理员登出成功");
            adminLogMapper.insert(log);
        }
        
        // 使令牌失效
        jwtTokenUtil.invalidateToken(token);
    }

    @Override
    public UserStatisticsResponse getUserStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        return UserStatisticsResponse.builder()
                .totalUsers(studentUserMapper.countTotalUsers())
                .todayNewUsers(studentUserMapper.countNewUsersAfter(todayStart))
                .monthlyActiveUsers(studentUserMapper.countActiveUsersAfter(monthStart))
                .normalUsers(studentUserMapper.countUsersByStatus(1))
                .disabledUsers(studentUserMapper.countUsersByStatus(0))
                .inactiveUsers(studentUserMapper.countUsersByStatus(2))
                .statisticsTime(now)
                .build();
    }
    
    @Override
    public PageInfo<StudentUser> getUserList(Integer pageNum, Integer pageSize, String username, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudentUser> users = studentUserMapper.selectByCondition(username, status, null, null);
        return new PageInfo<>(users);
    }
}