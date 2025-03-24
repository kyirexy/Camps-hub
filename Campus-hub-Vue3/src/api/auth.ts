export interface UserInfoDTO {
  userId: number
  username: string
  realName: string
  studentNumber: string
  collegeId: number
  collegeName: string
  major: string
  grade: string
  phone?: string
  email?: string
  bio?: string
  avatar?: string
}

export async function getUserInfo(userId: string) {
  // 实际应替换为axios请求
  return {
    data: {
      userId: 1,
      username: 'test_user',
      realName: '测试用户',
      studentNumber: '20230001'
    } as UserInfoDTO
  }
}