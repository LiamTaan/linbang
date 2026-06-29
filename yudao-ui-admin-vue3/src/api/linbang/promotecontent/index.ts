import request from '@/config/axios'

export interface PromoteContent {
  id: number
  promoterId?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  title?: string
  content?: string
  imageUrls?: string
  status?: string
  systemAuditResult?: string
  systemAuditRemark?: string
  systemAuditTime?: string
  manualAuditResult?: string
  manualAuditRemark?: string
  manualAuditBy?: number
  manualAuditTime?: string
  rejectReason?: string
  offlineReason?: string
  createTime?: string
}

export const PromoteContentApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/promote/content/page', params })
  },
  get: async (id: number) => {
    return await request.get<PromoteContent>({ url: `/promote/content/get?id=${id}` })
  },
  audit: async (data: any) => {
    return await request.post({ url: '/promote/content/audit', data })
  },
  offline: async (data: any) => {
    return await request.post({ url: '/promote/content/offline', data })
  }
}
