import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi, type UserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  // 设置用户信息
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    token.value = null
    localStorage.removeItem('token')
  }

  // 获取用户信息
  const fetchUserInfo = async (userId: number) => {
    try {
      const res = await userApi.getUserInfo(userId)
      setUserInfo(res)
      return res
    } catch (error) {
      ElMessage.error('获取用户信息失败')
      throw error
    }
  }

  // 用户登出
  const logout = async () => {
    try {
      await userApi.logout()
      clearUserInfo()
      ElMessage.success('登出成功')
    } catch (error) {
      ElMessage.error('登出失败')
      throw error
    }
  }

  return {
    userInfo,
    token,
    setUserInfo,
    setToken,
    clearUserInfo,
    fetchUserInfo,
    logout
  }
}) 