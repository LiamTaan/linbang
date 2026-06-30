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

export interface HelpFaq {
  id?: number
  categoryCode?: string
  categoryName?: string
  title?: string
  content?: string
  icon?: string
  sortNo?: number
  status?: string
  createTime?: string | Dayjs
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

export const HelpFaqApi = {
  getHelpFaqPage: async (params: any) => {
    return await request.get({ url: '/help/faq/page', params })
  },

  getHelpFaq: async (id: number) => {
    return await request.get<HelpFaq>({ url: `/help/faq/get?id=${id}` })
  },

  createHelpFaq: async (data: HelpFaq) => {
    return await request.post({ url: '/help/faq/create', data })
  },

  updateHelpFaq: async (data: HelpFaq) => {
    return await request.put({ url: '/help/faq/update', data })
  },

  deleteHelpFaq: async (id: number) => {
    return await request.delete({ url: `/help/faq/delete?id=${id}` })
  },

  exportHelpFaq: async (params: any) => {
    return await request.download({ url: '/help/faq/export-excel', params })
  }
}
