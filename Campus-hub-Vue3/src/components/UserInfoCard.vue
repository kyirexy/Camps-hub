<template>
  <el-card class="user-info-card">
    <template #header>
      <div class="card-header">
        <el-avatar :size="60" :src="avatarUrl" />
        <div class="user-basic">
          <h3>{{ userInfo.realName }}</h3>
          <p class="username">@{{ userInfo.username }}</p>
        </div>
      </div>
    </template>

    <el-skeleton :loading="loading" animated>
      <div class="info-section">
        <div class="info-item">
          <el-icon><User /></el-icon>
          <span>学号：{{ userInfo.studentNumber }}</span>
        </div>
        <div class="info-item">
          <el-icon><School /></el-icon>
          <span>{{ userInfo.collegeName }} - {{ userInfo.major }}</span>
        </div>
        <div class="info-item">
          <el-icon><Calendar /></el-icon>
          <span>{{ userInfo.grade }}级</span>
        </div>
        <el-divider />
        <div class="contact-info">
          <el-tag v-if="userInfo.phone" type="info" class="contact-tag">
            <el-icon><Iphone /></el-icon>
            {{ userInfo.phone }}
          </el-tag>
          <el-tag v-if="userInfo.email" type="info" class="contact-tag">
            <el-icon><Message /></el-icon>
            {{ userInfo.email }}
          </el-tag>
        </div>
      </div>
    </el-skeleton>
  </el-card>
</template>

<script setup lang="ts">
import { defineProps, computed } from 'vue'
// 若找不到模块 "@/api/auth"，需要确认该模块是否存在。
// 若存在，检查 tsconfig.json 里的路径别名配置。
// 以下是示例，在 tsconfig.json 里添加路径别名映射：
// {
//   "compilerOptions": {
//     "baseUrl": ".",
//     "paths": {
//       "@/*": ["src/*"]
//     }
//   }
// }
// 若模块不存在，需创建对应的文件与类型声明。
import type { UserInfoDTO } from '../api/auth'

const props = defineProps({
  userInfo: {
    type: Object as () => UserInfoDTO,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const avatarUrl = computed(() => 
  props.userInfo.avatar || '/src/assets/images/default-avatar.svg'
)
</script>

<style scoped>
.user-info-card {
  max-width: 600px;
  margin: 20px auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-basic h3 {
  margin: 0;
  font-size: 1.5rem;
}

.username {
  color: #909399;
  margin: 5px 0 0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 12px 0;
  font-size: 1rem;
}

.contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.contact-tag {
  padding: 8px 12px;
}
</style>