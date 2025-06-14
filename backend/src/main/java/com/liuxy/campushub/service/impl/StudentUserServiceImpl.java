package com.liuxy.campushub.service.impl;

import com.liuxy.campushub.dto.*;
import com.liuxy.campushub.entity.StudentUser;
import com.liuxy.campushub.entity.Image;
import com.liuxy.campushub.mapper.StudentUserMapper;
import com.liuxy.campushub.mapper.ImageMapper;
import com.liuxy.campushub.service.StudentUserService;
import com.liuxy.campushub.utils.JwtTokenUtil;
import com.liuxy.campushub.util.SftpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class StudentUserServiceImpl implements StudentUserService {

    @Value("${upload.avatar.path:/data/images/avatars}")
    private String avatarUploadPath;

    @Value("${upload.avatar.url:http://117.72.104.119/avatars}")
    private String avatarUrlPrefix;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private SftpUtil sftpUtil;

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

        try {
            logger.debug("Creating new user entity for username: {}", request.getUsername());
            // 创建用户实体
            StudentUser studentUser = new StudentUser();
            // 基本信息
            studentUser.setUsername(request.getUsername());
            studentUser.setPassword(passwordEncoder.encode(request.getPassword()));
            studentUser.setRealName(request.getUsername()); // 默认使用用户名作为真实姓名
            // 生成12位的临时学号：T + 时间戳后8位 + 用户名前3位
            String timestamp = String.valueOf(System.currentTimeMillis());
            String tempStudentNumber = "T" + timestamp.substring(timestamp.length() - 8) + 
                request.getUsername().substring(0, Math.min(3, request.getUsername().length()));
            studentUser.setStudentNumber(tempStudentNumber);
            studentUser.setGender("O"); // 默认其他
            
            // 生成临时手机号：T + 时间戳后8位
            String tempPhone = "T" + timestamp.substring(timestamp.length() - 8);
            studentUser.setPhone(tempPhone);
            
            // 生成临时邮箱：T + 时间戳后8位 + @temp.edu
            String tempEmail = "T" + timestamp.substring(timestamp.length() - 8) + "@temp.edu";
            studentUser.setEmail(tempEmail);
            
            // 院系信息
            studentUser.setCollegeId(0); // 默认院系ID为0
            studentUser.setCollegeName("待完善"); // 默认院系名称
            studentUser.setMajor("待完善"); // 默认专业名称
            studentUser.setGrade(2024); // 默认当前年份
            
            // 系统信息
            studentUser.setUserRole(1); // 默认普通用户
            studentUser.setStatus(1); // 默认未激活状态
            studentUser.setBio(""); // 空字符串，后续完善
            studentUser.setJwPassword(null); // 教务密码默认为null
            studentUser.setCreditScore(100); // 默认信用分100
            studentUser.setAvatarImageId(1); // 默认头像
            studentUser.setRegisterTime(LocalDateTime.now());//时间为当前时间

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
        // 查询头像图片
        String avatarUrl = null;
        if (user.getAvatarImageId() != null) {
            Image image = imageMapper.selectById(Long.valueOf(user.getAvatarImageId()));
            if (image != null && image.getFilePath() != null) {
                avatarUrl = avatarUrlPrefix + "/" + image.getFilePath();
            }
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
                .avatarUrl(avatarUrl)
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

    @Override
    public UpdateResponse updateUserInfo(Long userId, UpdateUserRequest request) {
        StudentUser user = studentUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        try {
            studentUserMapper.update(user);
            return UpdateResponse.success("用户信息更新成功");
        } catch (Exception e) {
            throw new RuntimeException("更新用户信息失败：" + e.getMessage());
        }
    }

    @Override
    public UpdateResponse changePassword(Long userId, ChangePasswordRequest request) {
        StudentUser user = studentUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return UpdateResponse.failure("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        try {
            studentUserMapper.update(user);
            return UpdateResponse.success("密码修改成功");
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败：" + e.getMessage());
        }
    }

    @Override
    public UpdateResponse uploadAvatar(Long userId, MultipartFile file) {
        logger.info("开始上传头像 - 用户ID: {}, 文件名: {}, 大小: {} bytes, 类型: {}", 
            userId, file.getOriginalFilename(), file.getSize(), file.getContentType());

        // 验证用户是否存在
        StudentUser user = studentUserMapper.selectById(userId);
        if (user == null) {
            return UpdateResponse.failure("用户不存在");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return UpdateResponse.failure("只允许上传图片文件");
        }

        try {
            // 使用SFTP上传文件
            String filename = sftpUtil.uploadFile(file, avatarUploadPath);
            logger.info("头像上传成功 - 用户ID: {}, 文件名: {}", userId, filename);

            // 插入图片信息到数据库
            Image image = new Image();
            image.setFilePath(filename);
            image.setOriginName(file.getOriginalFilename());
            image.setFileSize((int) file.getSize());
            image.setStatus(1);
            image.setUploaderId(userId);
            image.setUsageType("avatar");
            image.setUploadTime(LocalDateTime.now());
            imageMapper.insert(image);

            // 更新用户头像ID
            user.setAvatarImageId(image.getId());
            studentUserMapper.update(user);

            return UpdateResponse.success("头像上传成功");
        } catch (Exception e) {
            logger.error("头像上传失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            return UpdateResponse.failure("头像上传失败: " + e.getMessage());
        }
    }
}