<template>
  <div class="classroom">
    <div class="classroom-header">
      <h1>空教室查询</h1>
    </div>

    <div class="search-panel">
      <el-form :model="searchForm" inline>
        <el-form-item label="教学楼">
          <el-select v-model="searchForm.building" placeholder="选择教学楼">
            <el-option
              v-for="building in buildings"
              :key="building.id"
              :label="building.name"
              :value="building.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="时间段">
          <el-select v-model="searchForm.timeSlot" placeholder="选择时间段">
            <el-option
              v-for="slot in timeSlots"
              :key="slot.id"
              :label="slot.name"
              :value="slot.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="result-panel" v-if="showResults">
      <el-table :data="classrooms" style="width: 100%">
        <el-table-column prop="room" label="教室" />
        <el-table-column prop="capacity" label="容量" />
        <el-table-column prop="facilities" label="设施">
          <template #default="{ row }">
            <el-tag 
              v-for="facility in row.facilities" 
              :key="facility"
              size="small"
              class="facility-tag"
            >
              {{ facility }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleReserve(row)">
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="reserveDialogVisible"
      title="预约教室"
      width="500px"
    >
      <el-form
        ref="reserveFormRef"
        :model="reserveForm"
        :rules="reserveRules"
        label-width="100px"
      >
        <el-form-item label="使用事由" prop="purpose">
          <el-input
            v-model="reserveForm.purpose"
            type="textarea"
            placeholder="请输入使用事由"
          />
        </el-form-item>

        <el-form-item label="使用人数" prop="numberOfPeople">
          <el-input-number
            v-model="reserveForm.numberOfPeople"
            :min="1"
            :max="200"
          />
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input
            v-model="reserveForm.contact"
            placeholder="请输入联系方式"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="reserveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReserve">
            确认预约
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const buildings = [
  { id: 1, name: '第一教学楼' },
  { id: 2, name: '第二教学楼' },
  { id: 3, name: '综合楼' },
  { id: 4, name: '实验楼' }
]

const timeSlots = [
  { id: 1, name: '上午 8:00-10:00' },
  { id: 2, name: '上午 10:00-12:00' },
  { id: 3, name: '下午 14:00-16:00' },
  { id: 4, name: '下午 16:00-18:00' },
  { id: 5, name: '晚上 19:00-21:00' }
]

const searchForm = reactive({
  building: '',
  date: '',
  timeSlot: ''
})

const showResults = ref(false)
const classrooms = ref([
  {
    room: '101',
    capacity: 60,
    facilities: ['多媒体', '空调']
  },
  {
    room: '202',
    capacity: 120,
    facilities: ['多媒体', '空调', '电脑']
  },
  {
    room: '305',
    capacity: 80,
    facilities: ['多媒体']
  }
])

const reserveDialogVisible = ref(false)
const reserveFormRef = ref<FormInstance>()
const reserveForm = reactive({
  purpose: '',
  numberOfPeople: 1,
  contact: ''
})

const reserveRules = reactive<FormRules>({
  purpose: [
    { required: true, message: '请输入使用事由', trigger: 'blur' },
    { min: 10, message: '事由描述不能少于10个字符', trigger: 'blur' }
  ],
  numberOfPeople: [
    { required: true, message: '请输入使用人数', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7 // 不能选择过去的日期
}

const handleSearch = () => {
  // TODO: 实现教室查询逻辑
  showResults.value = true
}

const handleReserve = (room: any) => {
  reserveDialogVisible.value = true
}

const submitReserve = async () => {
  if (!reserveFormRef.value) return
  
  await reserveFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现预约提交逻辑
      console.log('submit!', reserveForm)
      reserveDialogVisible.value = false
    } else {
      console.error('error submit!', fields)
    }
  })
}
</script>

<style scoped>
.classroom {
  padding: 2rem;
}

.classroom-header {
  margin-bottom: 2rem;
}

.search-panel {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.result-panel {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.facility-tag {
  margin-right: 0.5rem;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}
</style> 