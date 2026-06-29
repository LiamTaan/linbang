import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MatchPushBatch {
  id: number
  orderId?: number
  unitId?: number
  stageNo?: number
  pushBatchNo?: number
  radiusStartKm?: number
  radiusEndKm?: number
  plannedAt?: string | Dayjs
  expiredAt?: string | Dayjs
  status?: string
  triggerType?: string
}

export const MatchPushBatchApi = {
  getMatchPushBatchPage: async (params: any) => {
    return await request.get({ url: '/linbang/match/push-batch/page', params })
  },
  getMatchPushBatch: async (id: number) => {
    return await request.get<MatchPushBatch>({ url: `/linbang/match/push-batch/get?id=${id}` })
  }
}
