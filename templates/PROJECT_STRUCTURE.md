 # Campus Hub 项目结构说明

## 项目概述
Campus Hub 是一个校园服务平台，采用前后端分离架构，后端使用 Spring Boot 框架开发。

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
├── aidto/                       # AI 相关数据传输对象
├── common/                      # 公共组件
├── config/                      # 配置类
├── controller/                  # 控制器层
├── dto/                         # 数据传输对象
├── entity/                      # 实体类
├── enums/                       # 枚举类
├── exception/                   # 异常处理
├── filter/                      # 过滤器
├── handler/                     # 处理器
├── mapper/                      # MyBatis 映射器
├── model/                       # 模型类
├── security/                    # 安全相关
├── service/                     # 服务层
├── utils/                       # 工具类
└── vo/                          # 视图对象
```

### 资源文件结构 (src/main/resources)
```
src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境配置
└── application-prod.yml         # 生产环境配置
```

## 主要模块说明

### 1. 核心模块
- **controller**: 处理 HTTP 请求，实现 RESTful API
- **service**: 实现业务逻辑
- **entity**: 定义数据库实体
- **mapper**: MyBatis 数据访问层
- **dto/vo**: 数据传输对象和视图对象

### 2. 配置模块
- **config**: 包含 CORS、安全等配置
- **security**: 安全认证和授权相关

### 3. 工具模块
- **utils**: 通用工具类
- **aiutil**: AI 相关工具类
- **common**: 公共组件和常量

### 4. 异常处理
- **exception**: 自定义异常和全局异常处理
- **handler**: 异常处理器

### 5. 安全模块
- **security**: 安全配置和实现
- **filter**: 请求过滤器

## 技术栈
- 后端框架：Spring Boot
- 数据库：MySQL
- ORM：MyBatis
- 安全：Spring Security
- 构建工具：Maven

## 开发环境
- JDK 版本：1.8+
- Maven 版本：3.6+
- IDE：IntelliJ IDEA / VS Code

## 部署说明
1. 确保 MySQL 数据库已安装并运行
2. 配置 application-dev.yml 中的数据库连接信息
3. 使用 Maven 构建项目：`mvn clean package`
4. 运行 jar 包：`java -jar target/campushub-0.0.1-SNAPSHOT.jar`

## 注意事项
1. 开发时使用 application-dev.yml 配置
2. 生产环境使用 application-prod.yml 配置
3. 确保数据库连接信息正确配置
4. 注意跨域配置和安全性设置