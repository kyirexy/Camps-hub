package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.dto.*;
import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.mapper.StudentUserMapper;
import com.liuxy.campushub.service.StudentUserService;
import com.liuxy.campushub.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentUserServiceImpl implements StudentUserService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger logger = LoggerFactory.getLogger(StudentUserServiceImpl.class);

    @Autowired
    private StudentUserMapper studentUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        logger.debug("Starting registration process for username: {}", request.getUsername());

        // 检查用户名是否已存在
        if (studentUserMapper.findByUsername(request.getUsername()) != null) {
            logger.info("Username already exists: {}", request.getUsername());
            return RegisterResponse.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }

        // 检查学号是否已存在
        if (studentUserMapper.findByStudentNumber(request.getStudentNumber()) != null) {
            logger.info("Student number already exists: {}", request.getStudentNumber());
            return RegisterResponse.builder()
                    .success(false)
                    .message("学号已被注册")
                    .build();
        }

        // 检查手机号是否已存在
        if (studentUserMapper.findByPhone(request.getPhone()) != null) {
            logger.info("Phone number already exists: {}", request.getPhone());
            return RegisterResponse.builder()
                    .success(false)
                    .message("手机号已被注册")
                    .build();
        }

        // 检查邮箱是否已存在
        if (studentUserMapper.findByEmail(request.getEmail()) != null) {
            logger.info("Email already exists: {}", request.getEmail());
            return RegisterResponse.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
        }

        try {
            logger.debug("Creating new user entity for username: {}", request.getUsername());
            // 创建用户实体
            StudentUser studentUser = new StudentUser();
            studentUser.setUsername(request.getUsername());
            studentUser.setPassword(passwordEncoder.encode(request.getPassword()));
            studentUser.setRealName(request.getRealName());
            studentUser.setStudentNumber(request.getStudentNumber());
            studentUser.setGender(request.getGender());
            studentUser.setPhone(request.getPhone());
            studentUser.setEmail(request.getEmail());
            studentUser.setCollegeId(request.getCollegeId());
            studentUser.setMajor(request.getMajor());
            studentUser.setGrade(request.getGrade());
            studentUser.setUserRole(1); // 默认普通用户
            studentUser.setStatus(1); // 默认正常状态
            studentUser.setCreditScore(100); // 默认信用分
            studentUser.setBio(request.getBio());

            // 如果有教务系统密码，加密存储
            if (request.getJwPassword() != null && !request.getJwPassword().isEmpty()) {
                studentUser.setJwPassword(passwordEncoder.encode(request.getJwPassword()));
            }

            logger.debug("Attempting to insert new user into database");
            // 保存用户
            int result = studentUserMapper.insert(studentUser);
            if (result > 0) {
                logger.info("Successfully registered new user: {}", request.getUsername());
                return RegisterResponse.builder()
                        .success(true)
                        .message("注册成功")
                        .userId(studentUser.getUserId())
                        .username(studentUser.getUsername())
                        .build();
            } else {
                logger.error("Failed to insert user into database: {}", request.getUsername());
                return RegisterResponse.builder()
                        .success(false)
                        .message("注册失败")
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error occurred during user registration: " + request.getUsername(), e);
            throw e;
        }
    }

    @Override
    public UserInfoDTO getUserInfoById(Long userId) {
        logger.debug("查询用户详细信息，用户ID：{}", userId);
        StudentUser user = studentUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return UserInfoDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .studentNumber(user.getStudentNumber())
                .collegeId(user.getCollegeId())
                .major(user.getMajor())
                .grade(user.getGrade())
                .phone(user.getPhone())
                .email(user.getEmail())
                .bio(user.getBio())
                .userRole(user.getUserRole())
                .status(user.getStatus())
                .build();
    }

    @Override
    public void logout(String token) {
        jwtTokenUtil.invalidateToken(token);
        logger.info("用户登出成功，令牌已失效");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.debug("开始处理登录请求，登录凭证：{}", request.getLoginId());
        
        // 根据登录凭证查询用户
        StudentUser user = studentUserMapper.findByLoginId(request.getLoginId());
        if (user == null) {
            logger.warn("登录失败：未找到与 [{}] 关联的用户", request.getLoginId());
            return LoginResponse.fail("用户不存在或凭证错误");
        }

        // 验证密码是否匹配
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("登录失败：用户 [{}] 密码不匹配", request.getLoginId());
            return LoginResponse.fail("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            logger.warn("登录失败：用户 [{}] 状态异常，当前状态：{}", 
                request.getLoginId(), user.getStatus());
            return LoginResponse.fail("账户状态异常，请联系管理员");
        }

        logger.info("用户 [{}] 登录成功", user.getUsername());
        String token = jwtTokenUtil.generateToken(user.getUserId(), user.getUsername(), user.getUserRole());
        return LoginResponse.success(user.getUserId(), user.getUsername(), user.getUserRole(), token);
    }
}