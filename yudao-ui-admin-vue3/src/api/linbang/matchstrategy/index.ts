import request from '@/config/axios'

export interface MatchStrategy {
  id?: number
  strategyCode: string
  strategyName: string
  stageConfigJson: string
  maxStageCount: number
  maxRadiusKm: number
  flowAdviceTemplate: string
  autoRefundEnabled: boolean
  autoRefundRetryTimes: number
  status?: string
}

export const MatchStrategyApi = {
  getMatchStrategy: async () => {
    return await request.get<MatchStrategy>({ url: '/linbang/match/strategy/get' })
  },
  updateMatchStrategy: async (data: MatchStrategy) => {
    return await request.put({ url: '/linbang/match/strategy/update', data })
  }
}
