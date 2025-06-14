# 学生用户列表接口文档

## 1. 获取学生列表
### 请求方法
GET

### 请求路径
/api/admin/users

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| pageNum | integer | 否 | 页码，默认为1 |
| pageSize | integer | 否 | 每页数量，默认为10 |
| username | string | 否 | 用户名模糊查询 |
| status | integer | 否 | 用户状态(0-禁用,1-启用) |

### 响应格式
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "list": [
    {
      "userId": 1,
      "username": "student2023",
      "realName": "张三",
      "studentNumber": "202303010001",
      "gender": "M",
      "phone": "+8613812345678",
      "email": "student2023@university.edu",
      "collegeId": 1001,
      "collegeName": "计算机学院",
      "major": "计算机科学与技术",
      "grade": 2023,
      "userRole": 1,
      "registerTime": "2023-03-21 08:49:56",
      "lastLogin": "2023-03-22 10:30:45",
      "avatarUrl": "https://example.com/avatar/123.jpg",
      "bio": "个人简介",
      "status": 1,
      "creditScore": 100
    }
  ]
}
```

## 2. 修改用户状态
### 请求方法
PUT

### 请求路径
/api/admin/users/status

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | integer | 是 | 用户ID |
| status | integer | 是 | 新状态(0-禁用,1-启用) |

### 响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 错误码
| 错误码 | 描述 |
|--------|------|
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 用户不存在 |
| 500 | 服务器内部错误 |