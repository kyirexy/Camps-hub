<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showCard = ref(false)

// TODO: 需要对接获取用户信息的API
const userInfo = ref({
  avatarUrl: '',
  username: '未登录',
  studentNumber: '未绑定',
  college: '未知学院'
})

const navigateToProfile = () => {
  router.push('/profile')
}
</script>

<template>
  <div class="avatar-container" @mouseenter="showCard = true" @mouseleave="showCard = false">
    <img 
      :src="userInfo.avatarUrl || '/src/assets/images/default-avatar.svg'"
      class="avatar-image"
      alt="用户头像"
      @click="navigateToProfile"
    />
    
    <transition name="fade">
      <div v-if="showCard" class="profile-card">
        <div class="card-header">
          <img 
            :src="userInfo.avatarUrl || '/src/assets/images/default-avatar.svg'"
            class="card-avatar"
            alt="用户头像"
          />
          <div class="user-info">
            <h3>{{ userInfo.username }}</h3>
            <p>{{ userInfo.studentNumber }}</p>
          </div>
        </div>
        <div class="card-body">
          <p>学院：{{ userInfo.college }}</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.avatar-container {
  position: relative;
  cursor: pointer;
}

.avatar-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #fff;
  transition: transform 0.2s;
}

.avatar-image:hover {
  transform: scale(1.1);
}

.profile-card {
  position: absolute;
  right: 0;
  top: 50px;
  width: 280px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  padding: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.card-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 12px;
}

.user-info h3 {
  margin: 0;
  font-size: 16px;
  color: #18191c;
}

.user-info p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #9499a0;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>