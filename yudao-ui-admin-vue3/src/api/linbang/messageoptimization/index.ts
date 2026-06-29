import request from '@/config/axios'

export interface MessageOptimization {
  id: number
  refType?: string
  templateId?: number
  campaignId?: number
  sceneCode?: string
  messageCategory?: string
  channelType?: string
  statStartDate?: string
  statEndDate?: string
  reachRate?: number
  clickRate?: number
  optimizationNote?: string
  nextAction?: string
  owner?: string
  deadline?: string
  updateTime?: string
}

export const MessageOptimizationApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/message/optimization/page', params })
  },
  get: async (id: number) => {
    return await request.get<MessageOptimization>({ url: `/message/optimization/get?id=${id}` })
  },
  save: async (data: Partial<MessageOptimization> & { id: number }) => {
    return await request.put({ url: '/message/optimization/save', data })
  }
}
