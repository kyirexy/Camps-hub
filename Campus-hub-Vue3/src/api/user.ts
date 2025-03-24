import request from '@/utils/request'

// 用户登录接口
export interface LoginParams {
  loginId: string
  password: string
}

export interface LoginResponse {
  success: boolean
  message: string
  userId: number
  username: string
  userRole: number
  token: string
}

// 用户注册接口
export interface RegisterParams {
  username: string
  password: string
  realName: string
  studentNumber: string
  gender?: 'M' | 'F' | 'O'
  phone: string
  email: string
  collegeId: number
  major: string
  grade: number
  bio?: string
  jwPassword: string
}

export interface RegisterResponse {
  success: boolean
  message: string
  userId: number
  username: string
}

// 用户信息接口
export interface UserInfo {
  userId: string
  username: string
  realName: string
  studentNumber: string
  gender: 'M' | 'F'
  phone: string
  email: string
  collegeId: string
  collegeName: string | null
  major: string
  grade: string
  userRole: number
  registerTime: string
  lastLogin: string | null
  avatarUrl: string
  bio: string
  status: number
  creditScore: number
}

// API方法
export const userApi = {
  // 用户登录
  login(data: LoginParams) {
    return request.post<LoginResponse>('/api/v1/student/login', data)
  },

  // 用户注册
  register(data: RegisterParams) {
    return request.post<RegisterResponse>('/api/v1/student/register', data)
  },

  // 获取用户信息
  getUserInfo: (userId: string) => {
    return request.get<UserInfo>(`/api/v1/student/${userId}`)
  },

  // 更新用户信息
  updateUserInfo: (data: Partial<UserInfo>) => {
    return request.put('/api/v1/student/update', data)
  },

  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }) => {
    return request.put('/api/v1/student/password', data)
  },

  // 用户登出
  logout() {
    return request.post('/api/v1/student/logout')
  }
} 