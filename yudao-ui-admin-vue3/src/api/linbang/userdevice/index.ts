import request from '@/config/axios'

export interface UserDevice {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  deviceFingerprint?: string
  deviceName?: string
  lastIp?: string
  lastLoginTime?: string
  status?: string
  createTime?: string
}

export const UserDeviceApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/user-device/page', params })
  }
}
