import request from '@/config/axios'

export interface PromoterPenaltyRecord {
  id: number
  promoterId?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  contentId?: number
  contentTitle?: string
  penaltyAction?: string
  scoreChange?: number
  reason?: string
  status?: string
  createTime?: string
}

export const PromoterPenaltyRecordApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/promote/penalty/page', params })
  },
  get: async (id: number) => {
    return await request.get<PromoterPenaltyRecord>({ url: `/promote/penalty/get?id=${id}` })
  }
}
