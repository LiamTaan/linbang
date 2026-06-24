import request from '@/config/axios'

export interface Blacklist {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  blackType?: string
  reason?: string
  startTime?: string
  endTime?: string
  status?: string
  createTime?: string
}

export interface BlacklistDetail extends Blacklist {
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string
    lastLoginIp?: string
  }
  recentRiskEvents?: {
    id?: number
    bizType?: string
    bizId?: number
    riskType?: string
    riskLevel?: string
    hitRuleCode?: string
    status?: string
    handleBy?: number
    handleTime?: string
    remark?: string
    createTime?: string
  }[]
}

export const BlacklistApi = {
  getBlacklistPage: async (params: any) => {
    return await request.get({ url: '/risk/blacklist/page', params })
  },

  getBlacklist: async (id: number) => {
    return await request.get<BlacklistDetail>({ url: `/risk/blacklist/get?id=${id}` })
  }
}
