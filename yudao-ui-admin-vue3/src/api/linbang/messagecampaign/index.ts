import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface MessageCampaign {
  id?: number
  campaignName: string
  sourceType: string
  targetMode: string
  targetRegionCodes?: string
  targetCategoryIds?: string
  targetRoleCodes?: string
  deliveryTimeWindows?: string
  scheduleTime?: string
  sceneCode: string
  messageCategory: string
  bizType?: string
  bizId?: number
  contentSnapshot: string
  auditStatus?: string
  executeStatus?: string
  plannedAudienceCount?: number
  reachedCount?: number
  clickedCount?: number
  readCount?: number
  voicePlayedCount?: number
  executeTime?: string
  createTime?: string
}

export const MessageCampaignApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/message/campaign/page', params })
  },
  get: async (id: number) => {
    return await request.get<MessageCampaign>({ url: `/message/campaign/get?id=${id}` })
  },
  create: async (data: MessageCampaign) => {
    return await request.post({ url: '/message/campaign/create', data })
  },
  approve: async (id: number, auditRemark?: string, verifyToken?: string) => {
    return await request.post({
      url: '/message/campaign/approve',
      data: { id, auditRemark },
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },
  reject: async (id: number, rejectReason: string, verifyToken?: string) => {
    return await request.post({
      url: '/message/campaign/reject',
      data: { id, rejectReason },
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },
  executeNow: async (id: number, verifyToken?: string) => {
    return await request.post({
      url: `/message/campaign/execute-now?id=${id}`,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },
  cancel: async (id: number, reason?: string) => {
    return await request.put({ url: '/message/campaign/cancel', data: { id, reason } })
  }
}
