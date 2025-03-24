<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import type { UserInfo } from '@/api/user'

const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息
const userInfo = ref<UserInfo>({} as UserInfo)
const isEditing = ref(false)
const loading = ref(false)
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 表单数据
const formData = ref({
  username: '',
  nickname: '',
  realName: '',
  studentNumber: '',
  gender: '',
  phone: '',
  email: '',
  collegeName: '',
  major: '',
  grade: '',
  bio: ''
})

// 密码表单
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符之间', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 30, message: '真实姓名不能超过30个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  bio: [
    { max: 200, message: '个人简介不能超过200个字符', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    if (userStore.userInfo?.userId) {
      const res = await userApi.getUserInfo(userStore.userInfo.userId)
      userInfo.value = res.data
      // 更新表单数据
      Object.assign(formData.value, {
        username: res.data.username,
        realName: res.data.realName,
        studentNumber: res.data.studentNumber,
        gender: res.data.gender,
        phone: res.data.phone,
        email: res.data.email,
        collegeName: res.data.collegeName || '',
        major: res.data.major,
        grade: res.data.grade,
        bio: res.data.bio
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 编辑资料
const handleEdit = () => {
  isEditing.value = true
}

// 取消编辑
const handleCancel = () => {
  isEditing.value = false
  // 重置表单数据
  Object.assign(formData.value, userInfo.value)
}

// 保存修改
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const updateData = {
      realName: formData.value.realName,
      phone: formData.value.phone,
      email: formData.value.email,
      bio: formData.value.bio
    }
    
    await userApi.updateUserInfo(updateData)
    ElMessage.success('保存成功')
    isEditing.value = false
    await fetchUserInfo() // 刷新用户信息
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}

// 修改密码
const handleChangePassword = () => {
  passwordDialogVisible.value = true
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

// 保存密码
const handleSavePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    loading.value = true
    
    await userApi.changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error('密码修改失败')
  } finally {
    loading.value = false
  }
}

// 绑定手机
const handleBindPhone = () => {
  // TODO: 实现手机绑定功能
  ElMessage.info('手机绑定功能开发中')
}

// 绑定邮箱
const handleBindEmail = () => {
  // TODO: 实现邮箱绑定功能
  ElMessage.info('邮箱绑定功能开发中')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <div class="profile-container">
    <div class="profile-header">
      <div class="profile-cover">
        <div class="profile-avatar">
          <el-avatar 
            :size="120" 
            :src="userInfo.avatarUrl || defaultAvatar"
            @error="() => true"
          />
        </div>
      </div>
      <div class="profile-info">
        <h1 class="profile-name">{{ userInfo.username }}</h1>
        <p class="profile-bio">{{ userInfo.bio || '这个人很懒，什么都没写~' }}</p>
        <div class="profile-meta">
          <span class="meta-item">
            <el-icon><School /></el-icon>
            {{ userInfo.collegeName || '未设置' }} · {{ userInfo.major }}
          </span>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ userInfo.grade }}级 · {{ userInfo.studentNumber }}
          </span>
          <span class="meta-item">
            <el-icon><Star /></el-icon>
            信用分：{{ userInfo.creditScore }}
          </span>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-button 
              type="primary" 
              link 
              @click="handleEdit"
              v-if="!isEditing"
            >
              编辑资料
            </el-button>
          </div>
        </template>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          :disabled="!isEditing"
        >
          <el-form-item label="用户名">
            <el-input v-model="formData.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="formData.realName" />
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="formData.studentNumber" disabled />
          </el-form-item>
          <el-form-item label="性别">
            <el-input v-model="formData.gender" disabled />
          </el-form-item>
          <el-form-item label="学院">
            <el-input v-model="formData.collegeName" disabled />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="formData.major" disabled />
          </el-form-item>
          <el-form-item label="年级">
            <el-input v-model="formData.grade" disabled />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="formData.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="formData.bio"
              type="textarea"
              :rows="4"
              placeholder="介绍一下你自己吧~"
            />
          </el-form-item>

          <el-form-item v-if="isEditing">
            <el-button type="primary" @click="handleSave" :loading="loading">
              保存修改
            </el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="security-card">
        <template #header>
          <div class="card-header">
            <span>安全设置</span>
            <el-button type="primary" link @click="handleChangePassword">
              修改密码
            </el-button>
          </div>
        </template>
        <div class="security-items">
          <div class="security-item">
            <div class="item-info">
              <h3>登录密码</h3>
              <p>定期更换密码可以保护账号安全</p>
            </div>
            <el-button type="primary" link @click="handleChangePassword">
              修改
            </el-button>
          </div>
          <div class="security-item">
            <div class="item-info">
              <h3>手机绑定</h3>
              <p>已绑定手机：{{ userInfo.phone || '未绑定' }}</p>
            </div>
            <el-button type="primary" link @click="handleBindPhone">
              {{ userInfo.phone ? '更换' : '绑定' }}
            </el-button>
          </div>
          <div class="security-item">
            <div class="item-info">
              <h3>邮箱绑定</h3>
              <p>已绑定邮箱：{{ userInfo.email || '未绑定' }}</p>
            </div>
            <el-button type="primary" link @click="handleBindEmail">
              {{ userInfo.email ? '更换' : '绑定' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePassword" :loading="loading">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.profile-header {
  margin-bottom: 24px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.profile-cover {
  height: 200px;
  background: linear-gradient(135deg, #00a1d6 0%, #e942a1 100%);
  position: relative;
}

.profile-avatar {
  position: absolute;
  bottom: -60px;
  left: 40px;
  border: 4px solid white;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.profile-info {
  padding: 80px 40px 40px;
}

.profile-name {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px;
}

.profile-bio {
  font-size: 14px;
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.profile-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.info-card,
.security-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.security-items {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.item-info h3 {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px;
}

.item-info p {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border: 1px solid #dcdfe6;
}

:deep(.el-input__wrapper:hover) {
  border-color: #409eff;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64,158,255,0.2);
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }

  .profile-avatar {
    left: 50%;
    transform: translateX(-50%);
  }

  .profile-info {
    text-align: center;
    padding-top: 80px;
  }
}

.profile-meta {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  color: #606266;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item .el-icon {
  font-size: 16px;
  color: #909399;
}
</style>