<template>
  <div class="course">
    <div class="course-header">
      <h1>课程助手</h1>
      <el-button type="primary" @click="importSchedule">
        导入课表
      </el-button>
    </div>

    <div class="schedule-container">
      <div class="week-selector">
        <el-button-group>
          <el-button 
            v-for="week in weeks" 
            :key="week"
            :type="currentWeek === week ? 'primary' : ''"
            @click="currentWeek = week"
          >
            第{{ week }}周
          </el-button>
        </el-button-group>
      </div>

      <div class="timetable">
        <div class="time-column">
          <div class="time-cell header"></div>
          <div v-for="time in times" :key="time.id" class="time-cell">
            <span>{{ time.start }}</span>
            <span>{{ time.end }}</span>
          </div>
        </div>

        <div class="day-grid">
          <div class="day-row header">
            <div v-for="day in days" :key="day" class="day-cell">
              {{ day }}
            </div>
          </div>
          <div v-for="time in times" :key="time.id" class="day-row">
            <div 
              v-for="day in days" 
              :key="day" 
              class="day-cell"
              :class="{ 'has-course': hasCourse(day, time.id) }"
              @click="showCourseDetail(day, time.id)"
            >
              <template v-if="hasCourse(day, time.id)">
                <div class="course-info">
                  <span class="course-name">{{ getCourse(day, time.id).name }}</span>
                  <span class="course-location">{{ getCourse(day, time.id).location }}</span>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="course-tools">
      <el-card class="tool-card">
        <template #header>
          <div class="card-header">
            <span>学习计划</span>
            <el-button type="primary" link>添加计划</el-button>
          </div>
        </template>
        <div class="todo-list">
          <el-checkbox-group v-model="checkedTodos">
            <div v-for="todo in todos" :key="todo.id" class="todo-item">
              <el-checkbox :label="todo.id">{{ todo.content }}</el-checkbox>
              <span class="todo-deadline">{{ todo.deadline }}</span>
            </div>
          </el-checkbox-group>
        </div>
      </el-card>

      <el-card class="tool-card">
        <template #header>
          <div class="card-header">
            <span>课程资料</span>
            <el-button type="primary" link>上传资料</el-button>
          </div>
        </template>
        <div class="material-list">
          <div v-for="material in materials" :key="material.id" class="material-item">
            <el-icon><Document /></el-icon>
            <span>{{ material.name }}</span>
            <el-button type="primary" link size="small">下载</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Document } from '@element-plus/icons-vue'

const currentWeek = ref(1)
const weeks = Array.from({ length: 20 }, (_, i) => i + 1)
const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const times = [
  { id: 1, start: '8:00', end: '9:35' },
  { id: 2, start: '9:55', end: '11:30' },
  { id: 3, start: '13:30', end: '15:05' },
  { id: 4, start: '15:25', end: '17:00' },
  { id: 5, start: '18:30', end: '20:05' }
]

const courses = ref([
  {
    id: 1,
    name: '高等数学',
    location: 'A301',
    day: '周一',
    time: 1
  },
  {
    id: 2,
    name: '大学英语',
    location: 'B205',
    day: '周二',
    time: 2
  }
])

const todos = ref([
  {
    id: 1,
    content: '完成高数作业',
    deadline: '今天 23:59'
  },
  {
    id: 2,
    content: '准备英语演讲',
    deadline: '明天 12:00'
  }
])

const materials = ref([
  {
    id: 1,
    name: '高等数学教学大纲.pdf',
    url: '#'
  },
  {
    id: 2,
    name: '英语课件Chapter 1.ppt',
    url: '#'
  }
])

const checkedTodos = ref([])

const hasCourse = (day: string, timeId: number) => {
  return courses.value.some(course => course.day === day && course.time === timeId)
}

const getCourse = (day: string, timeId: number) => {
  return courses.value.find(course => course.day === day && course.time === timeId)
}

const importSchedule = () => {
  // TODO: 实现导入课表功能
}

const showCourseDetail = (day: string, timeId: number) => {
  // TODO: 实现显示课程详情功能
}
</script>

<style scoped>
.course {
  padding: 2rem;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.schedule-container {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.week-selector {
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: center;
}

.timetable {
  display: flex;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.time-column {
  width: 100px;
  flex-shrink: 0;
}

.time-cell {
  height: 120px;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 0.9rem;
  color: #606266;
}

.time-cell.header {
  height: 50px;
}

.day-grid {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.day-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
}

.day-row.header {
  height: 50px;
}

.day-cell {
  flex: 1;
  height: 120px;
  border-right: 1px solid #ebeef5;
  padding: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.day-row.header .day-cell {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  background-color: #f5f7fa;
}

.day-cell.has-course {
  background-color: #ecf5ff;
}

.day-cell.has-course:hover {
  background-color: #d9ecff;
}

.course-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.course-name {
  font-weight: bold;
  color: #409eff;
}

.course-location {
  font-size: 0.9rem;
  color: #909399;
}

.course-tools {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.tool-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-deadline {
  font-size: 0.9rem;
  color: #909399;
}

.material-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.material-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
</style> 