<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="nav-left">
      <img src="@/assets/logo.svg" alt="logo" class="logo">
      <router-link 
        v-for="item in navItems" 
        :key="item.path"
        :to="item.path" 
        class="nav-item"
      >
        {{ item.label }}
      </router-link>
    </div>
    <div class="nav-right">
      <el-input
        v-model="searchText"
        placeholder="搜索服务..."
        :prefix-icon="Search"
        class="search-input"
        @keyup.enter="handleSearch"
      />
      <!-- 未登录状态 -->
      <div v-if="!isLogin" class="login-preview">
        <el-button 
          type="primary" 
          class="login-btn" 
          @click="showLoginDialog"
        >
          登录
        </el-button>
        <div class="preview-dropdown">
          <div class="preview-header">
            <h3>登录后即可使用</h3>
          </div>
          <div class="preview-content">
            <div v-for="feature in previewFeatures" :key="feature.title" class="preview-item">
              <el-icon><component :is="feature.icon" /></el-icon>
              <span>{{ feature.title }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 登录状态 -->
      <el-dropdown v-else trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar 
            :src="userStore.userInfo?.avatar || defaultAvatar"
            :size="40"
            class="user-avatar"
          />
          <span class="username">{{ userStore.userInfo?.nickname || '用户' }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </nav>

  <!-- 登录弹窗 -->
  <login-dialog
    v-model="loginDialogVisible"
    @success="handleLoginSuccess"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Search, User, Setting, SwitchButton, Calendar, ChatDotRound, Collection, ShoppingCart } from '@element-plus/icons-vue'
import LoginDialog from '@/components/LoginDialog.vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const searchText = ref('')
const loginDialogVisible = ref(false)
const isScrolled = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 计算属性：是否已登录
const isLogin = computed(() => !!userStore.token && !!userStore.userInfo)

// 导航菜单配置
const navItems = [
  { path: '/', label: '首页' },
  { path: '/market', label: '二手市场' },
  { path: '/course', label: '课程助手' },
  { path: '/classroom', label: '空教室' },
  { path: '/activity', label: '校园活动' }
]

// 预览功能列表
const previewFeatures = [
  { title: '课程管理', icon: 'Calendar' },
  { title: '校园社交', icon: 'ChatDotRound' },
  { title: '资源收藏', icon: 'Collection' },
  { title: '跑腿代购', icon: 'ShoppingCart' }
]

// 监听滚动事件
const handleScroll = () => {
  isScrolled.value = window.scrollY > 0
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 事件处理函数
const showLoginDialog = () => {
  loginDialogVisible.value = true
}

const handleLoginSuccess = () => {
  // 登录成功后刷新用户信息
  if (userStore.userInfo?.id) {
    userStore.fetchUserInfo(userStore.userInfo.id)
  }
}

const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('搜索:', searchText.value)
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      userStore.logout()
      break
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 48px;
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
}

.logo {
  height: 36px;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.nav-item {
  text-decoration: none;
  color: #606266;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  padding: 8px 0;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #409eff;
  transform: scaleX(0);
  transition: transform 0.3s ease;
  transform-origin: right;
}

.nav-item:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
  padding-left: 16px;
}

.search-input {
  width: 240px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  background-color: #e6f3ff;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  background-color: #ffffff;
  box-shadow: 0 0 0 2px rgba(64,158,255,0.2);
}

/* 登录按钮和预览 */
.login-preview {
  position: relative;
}

.login-btn {
  border-radius: 20px;
  padding: 8px 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff 0%, #007fff 100%);
  border: none;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 80px;
  text-align: center;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.3);
}

.login-btn:hover + .preview-dropdown {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}

.preview-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 280px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.3s ease;
  pointer-events: none;
  margin-top: 8px;
}

.preview-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.preview-header h3 {
  margin: 0;
  font-size: 16px;
  color: #1a1a1a;
}

.preview-content {
  padding: 16px;
}

.preview-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.preview-item:hover {
  background: #f5f7fa;
}

.preview-item .el-icon {
  font-size: 20px;
  color: #409eff;
}

.preview-item span {
  font-size: 14px;
  color: #606266;
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }

  .nav-left {
    gap: 16px;
  }

  .nav-item {
    font-size: 14px;
  }

  .search-input {
    width: 160px;
  }

  .login-btn {
    padding: 6px 16px;
    min-width: 60px;
  }

  .preview-dropdown {
    width: 240px;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 0 12px;
  }

  .nav-left {
    gap: 12px;
  }

  .nav-item {
    font-size: 13px;
  }

  .search-input {
    width: 120px;
  }

  .login-btn {
    padding: 6px 12px;
    min-width: 50px;
    font-size: 13px;
  }

  .preview-dropdown {
    width: 200px;
  }
}
</style> 