<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isLogin ? '登录' : '注册'"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :show-close="true"
    class="bilibili-dialog"
    center
  >
    <div class="dialog-content">
      <div class="dialog-layout">
        <!-- 左侧 - 情感化引导和功能权益 -->
        <div class="dialog-left">
          <div class="hero-section">
            <div class="hero-text">
              <h2>加入校园Hub，让美好迎面而来</h2>
              <p>发现精彩校园生活，连接志同道合的伙伴</p>
            </div>
            <div class="particles">
              <span v-for="n in 5" :key="n" class="particle">👍</span>
            </div>
          </div>

          <div class="features-section">
            <div class="feature-item">
              <el-icon class="feature-icon"><Calendar /></el-icon>
              <h3>课程管理</h3>
              <p>智能排课，轻松管理学习计划</p>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><ChatDotRound /></el-icon>
              <h3>校园社交</h3>
              <p>结识好友，分享校园生活</p>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Collection /></el-icon>
              <h3>资源收藏</h3>
              <p>收藏学习资源，随时查看</p>
            </div>
          </div>
        </div>

        <!-- 右侧 - 登录区域 -->
        <div class="dialog-right">
          <div class="login-section">
            <div class="login-methods">
              <div class="method-item qrcode" @click="switchToQRCode">
                <el-icon><Iphone /></el-icon>
                <span>扫码登录</span>
              </div>
              <div class="method-item password" @click="switchToPassword">
                <el-icon><Lock /></el-icon>
                <span>密码登录</span>
              </div>
              <div class="method-item register" @click="isLogin = false">
                <el-icon><User /></el-icon>
                <span>注册账号</span>
              </div>
            </div>

            <!-- 密码登录表单 -->
            <el-form
              v-if="!isQRCode"
              ref="formRef"
              :model="formData"
              :rules="formRules"
              class="login-form"
            >
              <el-form-item prop="loginId">
                <el-input
                  v-model="formData.loginId"
                  placeholder="用户名/手机号"
                  :prefix-icon="User"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="formData.password"
                  type="password"
                  placeholder="密码"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="primary">忘记密码？</el-link>
              </div>
              <el-button
                type="primary"
                class="submit-btn"
                :loading="loading"
                @click="handleSubmit"
              >
                {{ isLogin ? '登录' : '注册' }}
              </el-button>
            </el-form>

            <!-- 二维码登录 -->
            <div v-else class="qrcode-container">
              <div class="qrcode-box">
                <img src="https://api.dicebear.com/7.x/qr-codes/svg?seed=login" alt="登录二维码" />
              </div>
              <p class="qrcode-tip">请使用校园Hub APP扫码登录</p>
            </div>

            <!-- 第三方登录 -->
            <div class="third-party">
              <div class="divider">
                <span>其他登录方式</span>
              </div>
              <div class="social-login">
                <el-button circle class="social-btn wechat">
                  <i class="fab fa-weixin"></i>
                </el-button>
                <el-button circle class="social-btn qq">
                  <i class="fab fa-qq"></i>
                </el-button>
              </div>
            </div>

            <!-- 协议声明 -->
            <div class="agreement">
              登录即代表同意
              <el-link type="primary">《用户协议》</el-link>
              和
              <el-link type="primary">《隐私政策》</el-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Calendar,
  ChatDotRound,
  Collection,
  Iphone
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const isLogin = ref(true)
const isQRCode = ref(false)
const loading = ref(false)
const rememberMe = ref(false)

// 表单数据
const formData = ref({
  loginId: '',
  password: '',
  username: '',
  confirmPassword: '',
  realName: '',
  studentNumber: '',
  phone: '',
  email: '',
  jwPassword: ''
})

// 表单验证规则
const formRules = computed<FormRules>(() => ({
  loginId: [
    { required: true, message: '请输入用户名或手机号', trigger: 'blur' },
    { pattern: /^(\d{11}|[a-zA-Z0-9_]{4,20})$/, message: '用户名或手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 30, message: '真实姓名不能超过30个字符', trigger: 'blur' }
  ],
  studentNumber: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{12}$/, message: '学号必须是12位数字', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^\+?[1-9]\d{1,14}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.edu$/, message: '请输入有效的教育邮箱', trigger: 'blur' }
  ],
  jwPassword: [
    { required: true, message: '请输入教务系统密码', trigger: 'blur' }
  ]
}))

// 对话框显示状态
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 切换登录方式
const switchToQRCode = () => {
  isQRCode.value = true
}

const switchToPassword = () => {
  isQRCode.value = false
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true

    if (isLogin.value) {
      // 登录
      const res = await userApi.login({
        loginId: formData.value.loginId,
        password: formData.value.password
      })
      
      if (res.success) {
        handleLoginSuccess(res)
      }
    } else {
      // 注册
      const res = await userApi.register({
        username: formData.value.username,
        password: formData.value.password,
        realName: formData.value.realName,
        studentNumber: formData.value.studentNumber,
        phone: formData.value.phone,
        email: formData.value.email,
        collegeId: 1,
        major: '',
        grade: 2024,
        jwPassword: formData.value.jwPassword
      })
      
      if (res.success) {
        ElMessage.success('注册成功，请登录')
        isLogin.value = true
        formData.value = {
          loginId: '',
          password: '',
          username: '',
          confirmPassword: '',
          realName: '',
          studentNumber: '',
          phone: '',
          email: '',
          jwPassword: ''
        }
      }
    }
  } catch (error) {
    console.error('表单提交失败:', error)
  } finally {
    loading.value = false
  }
}

const handleLoginSuccess = async (response: any) => {
  // 设置token
  userStore.setToken(response.token)
  
  // 设置用户信息
  userStore.setUserInfo({
    id: response.userId,
    nickname: response.nickname,
    avatar: response.avatar,
    // ... 其他用户信息
  })

  // 触发成功事件
  emit('success')
  
  // 关闭对话框
  dialogVisible.value = false
}
</script>

<style scoped>
.bilibili-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
}

.bilibili-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.bilibili-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #18191c;
}

.bilibili-dialog :deep(.el-dialog__close) {
  font-size: 20px;
  color: #909399;
  transition: all 0.3s;
}

.bilibili-dialog :deep(.el-dialog__close:hover) {
  color: #00a1d6;
  transform: rotate(90deg);
}

.dialog-content {
  padding: 0;
}

.dialog-layout {
  display: flex;
  min-height: 600px;
}

/* 左侧布局 */
.dialog-left {
  flex: 1;
  background: linear-gradient(135deg, #00a1d6 0%, #e942a1 100%);
  color: white;
  display: flex;
  flex-direction: column;
}

/* 右侧布局 */
.dialog-right {
  flex: 1;
  padding: 40px;
  background: #fff;
}

/* 上半区 - 情感化引导 */
.hero-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.hero-text {
  position: relative;
  z-index: 1;
}

.hero-text h2 {
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 16px;
  line-height: 1.2;
}

.hero-text p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.5;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  font-size: 20px;
  animation: float 3s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 中部 - 功能权益说明 */
.features-section {
  padding: 40px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.feature-item {
  text-align: center;
  margin-bottom: 30px;
}

.feature-item:last-child {
  margin-bottom: 0;
}

.feature-icon {
  font-size: 40px;
  color: white;
  margin-bottom: 16px;
}

.feature-item h3 {
  font-size: 18px;
  margin: 0 0 8px;
  color: white;
}

.feature-item p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

/* 登录区域样式优化 */
.login-section {
  max-width: 400px;
  margin: 0 auto;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
}

.method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  min-width: 100px;
}

.method-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.method-item .el-icon {
  font-size: 28px;
  margin-bottom: 12px;
  color: #00a1d6;
}

.method-item span {
  font-size: 14px;
  color: #61666d;
}

.login-form {
  margin: 0 auto;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 22px;
  font-size: 16px;
  background: linear-gradient(135deg, #00a1d6 0%, #e942a1 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,161,214,0.3);
}

/* 二维码登录 */
.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
}

.qrcode-box {
  width: 240px;
  height: 240px;
  background: #f5f7fa;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.qrcode-box img {
  width: 220px;
  height: 220px;
}

.qrcode-tip {
  font-size: 14px;
  color: #61666d;
  margin: 0;
}

/* 第三方登录 */
.third-party {
  margin-top: 40px;
}

.divider {
  display: flex;
  align-items: center;
  color: #909399;
  margin: 24px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #dcdfe6;
}

.divider span {
  padding: 0 12px;
  font-size: 14px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.social-btn {
  width: 44px;
  height: 44px;
  border: none;
  transition: all 0.3s ease;
}

.social-btn.wechat {
  background: #07c160;
  color: white;
}

.social-btn.qq {
  background: #12b7f5;
  color: white;
}

.social-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 协议声明 */
.agreement {
  text-align: center;
  font-size: 12px;
  color: #909399;
  margin-top: 24px;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  background: #f5f7fa;
  box-shadow: none !important;
  border: 2px solid transparent;
  transition: all 0.3s;
  height: 44px;
}

:deep(.el-input__wrapper:hover) {
  background: #fff;
  border-color: #e4e7ed;
}

:deep(.el-input__wrapper.is-focus) {
  background: #fff;
  border-color: #00a1d6;
}

:deep(.el-input__inner) {
  height: 44px;
  font-size: 14px;
}

:deep(.el-input__prefix-icon) {
  font-size: 18px;
  color: #909399;
}
</style> 