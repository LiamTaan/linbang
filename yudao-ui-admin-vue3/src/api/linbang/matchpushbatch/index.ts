import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MatchPushBatch {
  id: number
  orderId?: number
  orderNo?: string
  orderStatus?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  unitId?: number
  unitNo?: string
  unitSeq?: number
  unitTitle?: string
  unitStatus?: string
  stageNo?: number
  pushBatchNo?: number
  radiusStartKm?: number
  radiusEndKm?: number
  plannedAt?: string | Dayjs
  expiredAt?: string | Dayjs
  status?: string
  triggerType?: string
  acceptedMerchantId?: number
  acceptedMerchantName?: string
  acceptedMerchantContactName?: string
  acceptedMerchantContactMobile?: string
  pushedMerchantCount?: number
  acceptedMatchCount?: number
  pushedMerchantNames?: string
}

export const MatchPushBatchApi = {
  getMatchPushBatchPage: async (params: any) => {
    return await request.get({ url: '/linbang/match/push-batch/page', params })
  },
  getMatchPushBatch: async (id: number) => {
    return await request.get<MatchPushBatch>({ url: `/linbang/match/push-batch/get?id=${id}` })
  }
}
