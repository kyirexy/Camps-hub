<template>
  <div class="home">
    <!-- Banner区域 -->
    <div class="banner">
      <div class="banner-content">
        <h1 class="banner-title">校园生活新方式</h1>
        <p class="banner-desc">一站式校园服务平台，让你的校园生活更轻松、更便捷</p>
        <el-button type="primary" class="try-btn" size="large" @click="showLoginDialog">
          立即体验
        </el-button>
      </div>
      <div class="banner-image">
        <img src="@/assets/hero-image.svg" alt="banner" />
      </div>
    </div>

    <!-- 服务卡片区域 -->
    <div class="services-section">
      <h2 class="section-title">热门服务</h2>
      <div class="services-grid">
        <el-card
          v-for="service in services"
          :key="service.id"
          class="service-card"
          :body-style="{ padding: '0px' }"
          shadow="hover"
        >
          <div class="service-image">
            <img :src="service.image" :alt="service.title">
          </div>
          <div class="service-content">
            <h3>{{ service.title }}</h3>
            <p>{{ service.description }}</p>
            <el-button 
              type="primary" 
              class="service-btn" 
              @click="handleServiceClick(service)"
            >
              立即使用
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <login-dialog
      v-model="loginDialogVisible"
      @success="handleLoginSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LoginDialog from '@/components/LoginDialog.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginDialogVisible = ref(false)

// 服务卡片配置
const services = [
  {
    id: 1,
    title: '二手交易',
    description: '闲置物品变现，为你的校园生活',
    image: '@/assets/images/market.svg',
    link: '/market'
  },
  {
    id: 2,
    title: '跑腿代购',
    description: '校园内快速配送，让生活更便捷',
    image: '@/assets/images/delivery.svg',
    link: '/delivery'
  },
  {
    id: 3,
    title: '空教室查询',
    description: '实时查询空闲教室，便捷预约使用',
    image: '@/assets/images/classroom.svg',
    link: '/classroom'
  },
  {
    id: 4,
    title: '课程助手',
    description: '智能规划学习，提高学习效率',
    image: '@/assets/images/course.svg',
    link: '/course'
  }
]

// 事件处理函数
const showLoginDialog = () => {
  loginDialogVisible.value = true
}

const handleLoginSuccess = () => {
  // TODO: 处理登录成功后的逻辑
  console.log('登录成功')
}

const handleServiceClick = (service: any) => {
  router.push(service.link)
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.banner {
  padding: 80px 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #e6f3ff 0%, #ffffff 100%);
  min-height: calc(100vh - 64px);
}

.banner-content {
  max-width: 500px;
}

.banner-title {
  font-size: 48px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 24px;
  line-height: 1.2;
}

.banner-desc {
  font-size: 18px;
  color: #606266;
  margin: 0 0 40px;
  line-height: 1.6;
}

.try-btn {
  padding: 16px 40px;
  font-size: 18px;
  font-weight: 500;
  border-radius: 28px;
  background: linear-gradient(135deg, #409eff 0%, #007fff 100%);
  border: none;
  transition: all 0.3s;
}

.try-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(64,158,255,0.3);
}

.banner-image {
  width: 50%;
  max-width: 600px;
}

.banner-image img {
  width: 100%;
  height: auto;
}

.services-section {
  padding: 80px 48px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 48px;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.service-card {
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
}

.service-card:hover {
  transform: translateY(-8px);
}

.service-image {
  height: 200px;
  overflow: hidden;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.service-card:hover .service-image img {
  transform: scale(1.05);
}

.service-content {
  padding: 24px;
}

.service-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px;
}

.service-content p {
  font-size: 14px;
  color: #606266;
  margin: 0 0 24px;
  line-height: 1.6;
}

.service-btn {
  width: 100%;
  border-radius: 8px;
  padding: 12px;
  font-size: 15px;
  font-weight: 500;
  background: linear-gradient(135deg, #409eff 0%, #007fff 100%);
  border: none;
}

.service-btn:hover {
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .banner {
    padding: 60px 24px;
    flex-direction: column;
    text-align: center;
  }

  .banner-content {
    margin-bottom: 40px;
  }

  .banner-image {
    width: 100%;
  }

  .services-section {
    padding: 60px 24px;
  }

  .section-title {
    font-size: 28px;
    margin-bottom: 32px;
  }
}
</style> 