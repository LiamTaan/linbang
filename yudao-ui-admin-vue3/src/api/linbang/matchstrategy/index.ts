import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface MatchStrategy {
  id?: number
  strategyCode: string
  strategyName: string
  stageConfigJson: string
  maxStageCount: number
  maxRadiusKm: number
  flowAdviceTemplate: string
  autoDispatchEnabled: boolean
  autoRefundEnabled: boolean
  autoRefundRetryTimes: number
  status?: string
}

export const MatchStrategyApi = {
  getMatchStrategy: async () => {
    return await request.get<MatchStrategy>({ url: '/linbang/match/strategy/get' })
  },
  updateMatchStrategy: async (data: MatchStrategy, verifyToken?: string) => {
    return await request.put({
      url: '/linbang/match/strategy/update',
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  }
}
