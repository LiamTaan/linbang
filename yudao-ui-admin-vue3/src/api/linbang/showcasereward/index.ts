import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface ShowcaseReward {
  id: number
  merchantId?: number
  userId?: number
  title?: string
  description?: string
  evidenceFileIdsJson?: string
  auditStatus?: string
  auditRemark?: string
  rejectReason?: string
  auditBy?: number
  auditTime?: string | Dayjs
  priorityEnabled?: boolean
  effectiveStartTime?: string | Dayjs
  effectiveEndTime?: string | Dayjs
  createTime?: string | Dayjs
}

export interface ShowcaseRewardAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export interface ShowcaseRewardParticipation {
  id: number
  rewardOrderId?: number
  rewardUserId?: number
  participantUserId?: number
  participantNickname?: string
  participantMobile?: string
  status?: string
  participateRemark?: string
  auditRemark?: string
  createTime?: string | Dayjs
}

export const ShowcaseRewardApi = {
  getShowcaseRewardPage: async (params: any) => {
    return await request.get({ url: '/linbang/showcase/reward/page', params })
  },
  getShowcaseReward: async (id: number) => {
    return await request.get<ShowcaseReward>({ url: `/linbang/showcase/reward/get?id=${id}` })
  },
  getParticipationList: async (rewardOrderId: number) => {
    return await request.get<ShowcaseRewardParticipation[]>({
      url: `/linbang/showcase/reward/participation/list?rewardOrderId=${rewardOrderId}`
    })
  },
  auditShowcaseReward: async (data: ShowcaseRewardAuditReqVO) => {
    return await request.post({ url: '/linbang/showcase/reward/audit', data })
  }
}
