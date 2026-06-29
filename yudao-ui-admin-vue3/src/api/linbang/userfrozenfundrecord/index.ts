import request from '@/config/axios'

export interface UserFrozenFundRecord {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  walletAccountId?: number
  frozenAmount?: number
  releasedAmount?: number
  status?: string
  sourceBizType?: string
  sourceBizId?: number
  reason?: string
  releasedBy?: number
  releasedTime?: string
  releaseRemark?: string
  createTime?: string
}

export const UserFrozenFundRecordApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/risk/user-frozen-fund-record/page', params })
  },
  get: async (id: number) => {
    return await request.get<UserFrozenFundRecord>({ url: `/risk/user-frozen-fund-record/get?id=${id}` })
  }
}
