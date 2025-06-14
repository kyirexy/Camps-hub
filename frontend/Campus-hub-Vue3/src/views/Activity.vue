<template>
  <div class="activity">
    <div class="activity-header">
      <h1>校园活动</h1>
      <el-button type="primary" @click="showPublishDialog">
        发布活动
      </el-button>
    </div>

    <div class="activity-filter">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部活动" name="all" />
        <el-tab-pane label="学术讲座" name="lecture" />
        <el-tab-pane label="文体活动" name="sports" />
        <el-tab-pane label="社团活动" name="club" />
        <el-tab-pane label="志愿服务" name="volunteer" />
      </el-tabs>
    </div>

    <div class="activity-list">
      <el-row :gutter="20">
        <el-col 
          v-for="activity in filteredActivities" 
          :key="activity.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <el-card class="activity-card" :body-style="{ padding: '0px' }">
            <img :src="activity.image" class="activity-image">
            <div class="activity-content">
              <h3>{{ activity.title }}</h3>
              <div class="activity-info">
                <p class="time">
                  <el-icon><Calendar /></el-icon>
                  {{ activity.time }}
                </p>
                <p class="location">
                  <el-icon><Location /></el-icon>
                  {{ activity.location }}
                </p>
              </div>
              <p class="description">{{ activity.description }}</p>
              <div class="activity-footer">
                <el-tag size="small" :type="activity.status === '报名中' ? 'success' : 'info'">
                  {{ activity.status }}
                </el-tag>
                <el-button type="primary" link @click="handleJoin(activity)">
                  {{ activity.status === '报名中' ? '立即报名' : '查看详情' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 发布活动弹窗 -->
    <el-dialog
      v-model="publishDialogVisible"
      title="发布活动"
      width="600px"
    >
      <el-form
        ref="publishFormRef"
        :model="publishForm"
        :rules="publishRules"
        label-width="100px"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="publishForm.title" placeholder="请输入活动标题" />
        </el-form-item>

        <el-form-item label="活动类型" prop="type">
          <el-select v-model="publishForm.type" placeholder="请选择活动类型">
            <el-option label="学术讲座" value="lecture" />
            <el-option label="文体活动" value="sports" />
            <el-option label="社团活动" value="club" />
            <el-option label="志愿服务" value="volunteer" />
          </el-select>
        </el-form-item>

        <el-form-item label="活动时间" prop="time">
          <el-date-picker
            v-model="publishForm.time"
            type="datetime"
            placeholder="请选择活动时间"
          />
        </el-form-item>

        <el-form-item label="活动地点" prop="location">
          <el-input v-model="publishForm.location" placeholder="请输入活动地点" />
        </el-form-item>

        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="publishForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>

        <el-form-item label="活动海报" prop="image">
          <el-upload
            class="activity-poster"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleImageChange"
          >
            <img v-if="imageUrl" :src="imageUrl" class="poster-preview">
            <el-icon v-else class="poster-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPublish">
            发布
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Calendar, Location, Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const activeTab = ref('all')
const publishDialogVisible = ref(false)
const publishFormRef = ref<FormInstance>()
const imageUrl = ref('')

const activities = ref([
  {
    id: 1,
    title: '2024春季校园招聘会',
    type: 'lecture',
    time: '2024-03-25 14:00',
    location: '大学生活动中心',
    description: '邀请多家知名企业来校招聘，提供丰富的就业机会',
    image: '/src/assets/images/job-fair.jpg',
    status: '报名中'
  },
  {
    id: 2,
    title: '校园歌手大赛',
    type: 'sports',
    time: '2024-04-15 19:00',
    location: '大礼堂',
    description: '一年一度的校园歌手盛典，等你来展示才艺',
    image: '/src/assets/images/singing.jpg',
    status: '报名中'
  },
  {
    id: 3,
    title: '志愿者服务活动',
    type: 'volunteer',
    time: '2024-03-30 09:00',
    location: '校园各处',
    description: '为校园环境保护贡献一份力量',
    image: '/src/assets/images/volunteer.jpg',
    status: '已结束'
  }
])

const publishForm = ref({
  title: '',
  type: '',
  time: '',
  location: '',
  description: '',
  image: ''
})

const publishRules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  time: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ]
})

const filteredActivities = computed(() => {
  if (activeTab.value === 'all') {
    return activities.value
  }
  return activities.value.filter(activity => activity.type === activeTab.value)
})

const handleTabClick = () => {
  // 可以添加额外的过滤逻辑
}

const showPublishDialog = () => {
  publishDialogVisible.value = true
}

const handleImageChange = (file: any) => {
  const isImage = file.raw.type.startsWith('image/')
  const isLt2M = file.raw.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('上传文件只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
    return false
  }

  imageUrl.value = URL.createObjectURL(file.raw)
}

const handleJoin = (activity: any) => {
  // TODO: 实现活动报名逻辑
}

const submitPublish = async () => {
  if (!publishFormRef.value) return
  
  await publishFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现活动发布逻辑
      console.log('submit!', publishForm.value)
      publishDialogVisible.value = false
    } else {
      console.error('error submit!', fields)
    }
  })
}
</script>

<style scoped>
.activity {
  padding: 2rem;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.activity-filter {
  margin-bottom: 2rem;
}

.activity-card {
  margin-bottom: 2rem;
  transition: transform 0.3s;
}

.activity-card:hover {
  transform: translateY(-5px);
}

.activity-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.activity-content {
  padding: 1rem;
}

.activity-info {
  margin: 1rem 0;
  color: #666;
  font-size: 0.9rem;
}

.activity-info p {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0.5rem 0;
}

.description {
  color: #666;
  font-size: 0.9rem;
  margin: 1rem 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
}

.activity-poster {
  width: 200px;
  height: 200px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.poster-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.poster-preview {
  width: 200px;
  height: 200px;
  object-fit: cover;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}
</style> 