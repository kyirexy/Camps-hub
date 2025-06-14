# 用户头像上传 API 文档

## 上传用户头像

### 接口说明
- 接口名称：上传用户头像
- 接口路径：`/api/v1/student/{userId}/avatar`
- 请求方式：POST
- 接口描述：上传用户头像图片，支持 JPG、JPEG、PNG、GIF 格式，图片将按年月组织存储
- 权限要求：需要用户登录，只能上传自己的头像（管理员可以上传任何用户的头像）

### 请求参数

#### 路径参数
| 参数名 | 类型   | 必填 | 说明     |
|--------|--------|------|----------|
| userId | number | 是   | 用户ID   |

#### 请求头
| 参数名        | 类型   | 必填 | 说明                          |
|---------------|--------|------|-------------------------------|
| Authorization | string | 是   | Bearer Token，格式：Bearer {token} |

#### 请求体
使用 `multipart/form-data` 格式

| 参数名 | 类型   | 必填 | 说明     |
|--------|--------|------|----------|
| file   | file   | 是   | 头像图片文件 |

### 响应参数

#### 成功响应
```json
{
    "success": true,
    "message": "头像上传成功"
}
```

#### 失败响应
```json
{
    "success": false,
    "message": "错误信息"
}
```

### 错误码说明
| 错误码 | 说明                 |
|--------|---------------------|
| 400    | 请求参数错误         |
| 401    | 未授权或token已过期  |
| 403    | 无权限操作其他用户头像 |
| 404    | 用户不存在           |
| 413    | 文件大小超过限制（最大20MB） |
| 415    | 不支持的文件类型     |
| 500    | 服务器内部错误       |

### 请求示例

#### cURL
```bash
curl -X POST \
  'http://117.72.104.119/api/v1/student/123/avatar' \
  -H 'Authorization: Bearer your-jwt-token' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/path/to/your/avatar.jpg'
```

#### JavaScript (Fetch)
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

// 检查文件大小
if (fileInput.files[0].size > 20 * 1024 * 1024) {
    showError('文件大小不能超过20MB');
    return;
}

fetch('http://117.72.104.119/api/v1/student/123/avatar', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your-jwt-token'
  },
  body: formData
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    // 上传成功，更新头像显示
    const avatarUrl = `http://117.72.104.119/avatars/${data.filePath}`;
    updateAvatarDisplay(avatarUrl);
  } else {
    // 处理错误
    showError(data.message);
  }
})
.catch(error => {
  if (error.message.includes('Maximum upload size exceeded')) {
    showError('文件大小不能超过20MB');
  } else {
    console.error('Error:', error);
    showError('上传失败，请稍后重试');
  }
});
```

#### JavaScript (Axios)
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

// 检查文件大小
if (fileInput.files[0].size > 20 * 1024 * 1024) {
    showError('文件大小不能超过20MB');
    return;
}

axios.post('http://117.72.104.119/api/v1/student/123/avatar', formData, {
  headers: {
    'Authorization': 'Bearer your-jwt-token',
    'Content-Type': 'multipart/form-data'
  }
})
.then(response => {
  if (response.data.success) {
    // 上传成功，更新头像显示
    const avatarUrl = `http://117.72.104.119/avatars/${response.data.filePath}`;
    updateAvatarDisplay(avatarUrl);
  } else {
    // 处理错误
    showError(response.data.message);
  }
})
.catch(error => {
  if (error.response && error.response.status === 413) {
    showError('文件大小不能超过20MB');
  } else {
    console.error('Error:', error);
    showError('上传失败，请稍后重试');
  }
});
```

### 文件存储说明
1. 文件存储路径：`/data/images/avatars/{yyyy}/{MM}/{filename}`
2. 访问URL格式：`http://117.72.104.119/avatars/{yyyy}/{MM}/{filename}`
3. 文件名格式：UUID + 原始文件扩展名
4. 文件大小限制：最大 20MB
5. 支持的文件类型：JPG、JPEG、PNG、GIF

### 注意事项
1. 上传前检查文件类型和大小
2. 建议在上传前对图片进行压缩和裁剪
3. 上传成功后，头像将通过 Nginx 服务器访问
4. 建议在前端缓存新的头像URL
5. 图片按年月组织存储，便于管理和清理
6. 所有图片信息都会记录在数据库中

### 最佳实践
1. 上传前检查
   - 验证文件类型
   - 验证文件大小（不超过20MB）
   - 验证图片尺寸（建议不超过 1024x1024）

2. 用户体验
   - 显示上传进度条
   - 提供图片预览功能
   - 支持图片裁剪
   - 显示文件大小和类型

3. 错误处理
   - 显示友好的错误提示
   - 处理网络超时
   - 处理文件类型错误
   - 处理大小超限
   - 处理服务器错误

4. 性能优化
   - 压缩图片后再上传
   - 使用适当的图片格式
   - 实现断点续传
   - 添加重试机制

### 更新日志
- 2024-03-21: 初始版本发布
- 2024-03-21: 更新文件存储结构说明
- 2024-03-21: 添加数据库存储说明
- 2024-03-21: 完善错误处理说明
- 2024-03-21: 更新文件大小限制为20MB 