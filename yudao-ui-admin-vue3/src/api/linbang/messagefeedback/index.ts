import request from '@/config/axios'

export interface MessageFeedbackStat {
  id: number
  statDate?: string
  sceneCode?: string
  messageCategory?: string
  templateId?: number
  campaignId?: number
  pushTaskId?: number
  channelType?: string
  plannedAudienceCount?: number
  reachedCount?: number
  clickedCount?: number
  readCount?: number
  voicePlayedCount?: number
  reachRate?: number
  clickRate?: number
  readRate?: number
}

export const MessageFeedbackApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/message/feedback/page', params })
  },
  get: async (id: number) => {
    return await request.get<MessageFeedbackStat>({ url: `/message/feedback/get?id=${id}` })
  }
}
