import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface HelpFeedback {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  feedbackType?: string
  content?: string
  contactMobile?: string
  attachmentUrls?: string
  status?: string
  handleBy?: number
  handleRemark?: string
  createTime?: string | Dayjs
}

export interface HelpFeedbackDetail extends HelpFeedback {
  updateTime?: string | Dayjs
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
  }
  summary?: {
    sameUserFeedbackCount?: number
    sameTypeFeedbackCount?: number
    pendingCount?: number
    processingCount?: number
    finishedCount?: number
  }
  relatedFeedbacks?: Array<{
    id?: number
    userId?: number
    feedbackType?: string
    contactMobile?: string
    status?: string
    handleRemark?: string
    createTime?: string | Dayjs
  }>
}

export const HelpFeedbackApi = {
  getHelpFeedbackPage: async (params: any) => {
    return await request.get({ url: '/help/feedback/page', params })
  },

  getHelpFeedback: async (id: number) => {
    return await request.get<HelpFeedbackDetail>({ url: `/help/feedback/get?id=${id}` })
  },

  exportHelpFeedback: async (params: any) => {
    return await request.download({ url: '/help/feedback/export-excel', params })
  }
}
