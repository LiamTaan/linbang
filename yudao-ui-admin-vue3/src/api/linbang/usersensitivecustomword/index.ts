import request from '@/config/axios'

export interface UserSensitiveCustomWord {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  word?: string
  sceneType?: string
  status?: string
  remark?: string
  createTime?: string
}

export const UserSensitiveCustomWordApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/user-sensitive-custom-word/page', params })
  }
}
