# Campus Hub 项目结构说明

## 项目概述
Campus Hub 是一个校园服务平台，采用前后端分离架构，后端使用 Spring Boot 框架开发。项目提供用户管理、帖子管理、评论系统、AI 服务等功能，支持图片上传和访问。

## 目录结构

### 根目录
```
Campus-hub/
├── src/                    # 源代码目录
├── target/                 # 编译输出目录
├── .mvn/                   # Maven 包装器配置
├── .vscode/               # VS Code 配置
├── .idea/                 # IntelliJ IDEA 配置
├── @api-docs/             # API 文档
├── pom.xml                # Maven 项目配置文件
├── mvnw                   # Maven 包装器脚本（Unix）
└── mvnw.cmd               # Maven 包装器脚本（Windows）
```

### 源代码结构 (src/main/java/com/liuxy/campushub)
```
src/main/java/com/liuxy/campushub/
├── CampusHubApplication.java    # 应用程序入口
├── aiutil/                      # AI 工具类
│   ├── AIService.java          # AI 服务接口
│   └── AIServiceImpl.java      # AI 服务实现
├── aidto/                       # AI 相关数据传输对象
│   ├── AIRequest.java          # AI 请求对象
│   └── AIResponse.java         # AI 响应对象
├── common/                      # 公共组件
│   ├── constants/              # 常量定义
│   │   ├── CommonConstants.java # 通用常量
│   │   └── SecurityConstants.java # 安全相关常量
│   ├── enums/                  # 枚举类型
│   │   ├── PostStatus.java     # 帖子状态枚举
│   │   └── UserRole.java       # 用户角色枚举
│   └── utils/                  # 通用工具类
│       ├── DateUtil.java       # 日期工具类
│       └── ValidationUtil.java # 验证工具类
├── config/                      # 配置类
│   ├── CorsConfig.java         # 跨域配置
│   ├── SecurityConfig.java     # 安全配置
│   ├── WebConfig.java          # Web 配置
│   └── RedisConfig.java        # Redis 配置
├── controller/                  # 控制器层
│   ├── PostController.java     # 帖子控制器
│   │   - 帖子发布、编辑、删除
│   │   - 帖子列表查询（瀑布流）
│   │   - 帖子详情查询
│   │   - 帖子点赞、收藏
│   ├── ProductController.java  # 商品控制器
│   │   - 商品发布、编辑、删除
│   │   - 商品列表查询
│   │   - 商品详情查询
│   │   - 商品状态管理
│   ├── AttachmentController.java # 附件控制器
│   │   - 文件上传
│   │   - 文件下载
│   │   - 文件删除
│   │   - 文件预览
│   ├── StudentUserController.java # 学生用户控制器
│   │   - 学生用户注册
│   │   - 学生信息管理
│   │   - 学生认证
│   │   - 学生权限管理
│   ├── WeatherController.java  # 天气控制器
│   │   - 天气信息查询
│   │   - 天气数据更新
│   ├── CommentController.java  # 评论控制器
│   │   - 评论发布、编辑、删除
│   │   - 评论列表查询
│   │   - 评论点赞
│   ├── AdminController.java    # 管理员控制器
│   │   - 管理员登录
│   │   - 系统管理
│   │   - 用户管理
│   │   - 内容审核
│   ├── AIChatController.java   # AI聊天控制器
│   │   - AI对话接口
│   │   - 聊天历史记录
│   │   - 对话上下文管理
│   ├── LostFoundController.java # 失物招领控制器
│   │   - 失物招领发布
│   │   - 物品认领
│   │   - 状态更新
│   │   - 列表查询
│   ├── CategoryController.java # 分类控制器
│   │   - 分类管理
│   │   - 分类树结构
│   │   - 分类关联
│   └── TopicController.java    # 话题控制器
│       - 话题创建、编辑
│       - 话题列表查询
│       - 话题关联
│       - 热门话题
├── dto/                         # 数据传输对象
│   ├── PostDTO.java           # 帖子 DTO
│   ├── CommentDTO.java        # 评论 DTO
│   ├── UserDTO.java           # 用户 DTO
│   └── AIDTO.java             # AI 相关 DTO
├── entity/                      # 实体类
│   ├── Post.java              # 帖子实体
│   ├── Comment.java           # 评论实体
│   ├── User.java              # 用户实体
│   └── BaseEntity.java        # 基础实体类
├── exception/                   # 异常处理
│   ├── BusinessException.java  # 业务异常
│   ├── GlobalExceptionHandler.java # 全局异常处理
│   └── ApiException.java       # API 异常
├── filter/                      # 过滤器
│   ├── AuthenticationFilter.java # 认证过滤器
│   └── LoggingFilter.java      # 日志过滤器
├── handler/                     # 处理器
│   ├── SecurityHandler.java     # 安全处理器
│   └── ResponseHandler.java     # 响应处理器
├── mapper/                      # MyBatis 映射器
│   ├── PostMapper.java         # 帖子数据访问
│   ├── CommentMapper.java      # 评论数据访问
│   ├── UserMapper.java         # 用户数据访问
│   └── BaseMapper.java         # 基础数据访问接口
├── model/                       # 模型类
│   ├── Result.java             # 统一响应模型
│   └── PageResult.java         # 分页响应模型
├── security/                    # 安全相关
│   ├── JwtUtil.java            # JWT 工具类
│   ├── SecurityUtil.java       # 安全工具类
│   └── PasswordEncoder.java    # 密码加密工具
├── service/                     # 服务层
│   ├── PostService.java        # 帖子服务接口
│   ├── CommentService.java     # 评论服务接口
│   ├── UserService.java        # 用户服务接口
│   ├── AIService.java          # AI 服务接口
│   └── impl/                   # 服务实现
│       ├── PostServiceImpl.java # 帖子服务实现
│       ├── CommentServiceImpl.java # 评论服务实现
│       ├── UserServiceImpl.java # 用户服务实现
│       └── AIServiceImpl.java  # AI 服务实现
├── utils/                       # 工具类
│   ├── FileUtil.java           # 文件处理工具
│   ├── StringUtil.java         # 字符串工具
│   ├── RedisUtil.java          # Redis 工具类
│   └── ImageUtil.java          # 图片处理工具
└── vo/                          # 视图对象
    ├── PostVO.java             # 帖子视图对象
    ├── CommentVO.java          # 评论视图对象
    ├── UserVO.java             # 用户视图对象
    └── AIVO.java               # AI 相关视图对象
```

### 资源文件结构 (src/main/resources)
```
src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境配置
├── application-prod.yml         # 生产环境配置
├── application-test.yml         # 测试环境配置
├── mapper/                      # MyBatis XML 映射文件
│   ├── PostMapper.xml          # 帖子映射
│   ├── CommentMapper.xml       # 评论映射
│   └── UserMapper.xml          # 用户映射
├── static/                      # 静态资源
│   ├── avatars/                # 用户头像存储目录
│   └── images/                 # 图片存储目录
└── templates/                   # 模板文件
    └── email/                  # 邮件模板
```

## 主要模块说明

### 1. 核心模块
- **controller**: 处理 HTTP 请求，实现 RESTful API
  - 提供帖子、评论、用户等接口
  - 实现请求参数验证和响应封装
  - 处理文件上传和下载
  - 实现分页查询和条件过滤
  - 支持多种业务模块：
    - 帖子管理（发布、编辑、删除、查询）
    - 商品管理（发布、编辑、状态管理）
    - 失物招领（发布、认领、状态更新）
    - 用户管理（注册、认证、权限控制）
    - AI 服务（智能对话、上下文管理）
    - 系统管理（分类、话题、内容审核）
  - 统一的响应格式和错误处理
  - 支持文件上传和图片处理
  - 实现权限控制和访问限制
- **service**: 实现业务逻辑
  - 处理核心业务逻辑
  - 实现事务管理
  - 调用数据访问层
  - 集成第三方服务（如 AI 服务）
- **entity**: 定义数据库实体
  - 对应数据库表结构
  - 包含实体间关系
  - 实现数据验证
  - 支持审计功能
- **mapper**: MyBatis 数据访问层
  - 实现数据库 CRUD 操作
  - 支持复杂查询
  - 实现动态 SQL
  - 处理关联查询
- **dto/vo**: 数据传输对象和视图对象
  - DTO: 用于服务层数据传输
  - VO: 用于前端展示数据
  - 实现数据转换和验证
  - 支持数据脱敏

### 2. 配置模块
- **config**: 包含系统配置
  - CORS 跨域配置
  - 安全配置
  - Web 相关配置
  - Redis 配置
  - 文件上传配置
- **security**: 安全认证和授权
  - JWT 认证
  - 权限控制
  - 安全工具类
  - 密码加密
  - 会话管理

### 3. 工具模块
- **utils**: 通用工具类
  - 文件处理
  - 字符串处理
  - 日期处理
  - Redis 操作
  - 图片处理
- **aiutil**: AI 相关工具
  - AI 服务接口
  - AI 请求处理
  - 结果解析
  - 错误处理
- **common**: 公共组件
  - 常量定义
  - 通用工具
  - 枚举类型
  - 验证工具

### 4. 异常处理
- **exception**: 异常处理机制
  - 自定义业务异常
  - 全局异常处理
  - 异常响应封装
  - 日志记录
- **handler**: 异常处理器
  - 统一异常处理
  - 错误响应格式化
  - 异常日志记录
  - 错误码管理

### 5. 安全模块
- **security**: 安全框架
  - JWT 实现
  - 权限控制
  - 安全工具
  - 密码加密
  - 会话管理
- **filter**: 请求过滤器
  - 认证过滤
  - 权限验证
  - 请求预处理
  - 日志记录
  - 性能监控

## 技术栈
- 后端框架：Spring Boot 2.7.x
- 数据库：MySQL 8.0
- ORM：MyBatis 3.5.x
- 安全：Spring Security + JWT
- 构建工具：Maven 3.6+
- 缓存：Redis 6.0+
- 文件存储：本地文件系统
- 日志：Logback + SLF4J
- 文档：Swagger/OpenAPI
- 测试：JUnit 5 + Mockito

## 开发环境
- JDK 版本：1.8+
- Maven 版本：3.6+
- IDE：IntelliJ IDEA / VS Code
- 数据库：MySQL 8.0
- Redis：6.0+
- Git：2.x+
- Node.js：14.x+（前端开发）

## 部署说明
1. 环境准备
   - 安装 JDK 1.8+
   - 安装 MySQL 8.0
   - 安装 Redis 6.0+
   - 配置 Maven 环境
   - 配置 Nginx（可选）

2. 数据库配置
   - 创建数据库：`campushub`
   - 执行数据库脚本
   - 配置数据库连接信息
   - 设置字符集为 utf8mb4

3. 项目配置
   - 修改 application-dev.yml 配置
   - 配置图片存储路径
   - 配置 Redis 连接信息
   - 配置日志输出
   - 配置跨域设置

4. 构建部署
   - 执行构建：`mvn clean package`
   - 运行服务：`java -jar target/campushub-0.0.1-SNAPSHOT.jar`
   - 访问服务：`http://localhost:8080`
   - 配置 Nginx 反向代理（可选）

## 注意事项
1. 开发规范
   - 遵循阿里巴巴 Java 开发规范
   - 使用统一的代码格式化配置
   - 保持代码注释完整
   - 编写单元测试
   - 使用 Git Flow 工作流

2. 安全规范
   - 敏感配置信息加密存储
   - 定期更新依赖版本
   - 做好权限控制
   - 防止 SQL 注入
   - 防止 XSS 攻击

3. 部署规范
   - 使用生产环境配置
   - 配置日志收集
   - 设置监控告警
   - 配置备份策略
   - 设置资源限制

4. 维护建议
   - 定期备份数据
   - 监控系统资源
   - 及时处理异常
   - 定期清理日志
   - 更新安全补丁