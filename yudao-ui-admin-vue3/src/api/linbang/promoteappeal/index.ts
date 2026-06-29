import request from '@/config/axios'

export interface PromoteAppeal {
  id: number
  contentId?: number
  contentTitle?: string
  promoterId?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  appealReason?: string
  status?: string
  auditRemark?: string
  rejectReason?: string
  auditBy?: number
  auditTime?: string
  createTime?: string
}

export const PromoteAppealApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/promote/appeal/page', params })
  },
  audit: async (data: any) => {
    return await request.post({ url: '/promote/appeal/audit', data })
  }
}
