import { ref } from 'vue'
// 若pinia版本支持，确保导入路径正确
import { defineStore } from 'pinia'
// 若pinia版本不支持defineStore，可考虑更新pinia版本
// npm install pinia@latest
import type { UserInfoDTO } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const isLogin = ref(false)
  const userInfo = ref<UserInfoDTO | null>(null)
  const token = ref('')

  function setLoginStatus(status: boolean) {
    isLogin.value = status
  }

  function setUserInfo(info: UserInfoDTO) {
    userInfo.value = info
  }

  function setToken(newToken: string) {
    token.value = newToken
  }

  return {
    isLogin,
    userInfo,
    token,
    setLoginStatus,
    setUserInfo,
    setToken,
  }
}, {
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'auth-store',
        storage: localStorage
      }
    ]
  }
})