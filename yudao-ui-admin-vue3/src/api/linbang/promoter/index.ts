import request from '@/config/axios'

export interface Promoter {
  id: number
  userId: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  levelCode?: string
  inviteCode?: string
  inviteUrl?: string
  bindUserCount?: number
  convertCount?: number
  totalCommissionAmount?: number
  availableCommissionAmount?: number
  status?: string
  createTime?: string
}

export interface PromoterDetail extends Promoter {
  updateTime?: string
  user?: PromoterUser
  summary?: PromoterSummary
  recentRelations?: PromoterRelation[]
  recentCommissionOrders?: PromoterCommissionOrder[]
}

export interface PromoterUser {
  id?: number
  userNo?: string
  mobile?: string
  nickname?: string
  currentRoleCode?: string
  status?: string
  lastLoginTime?: string
}

export interface PromoterSummary {
  relationCount?: number
  convertedRelationCount?: number
  pendingCommissionCount?: number
  settledCommissionCount?: number
  invalidCommissionCount?: number
  pendingCommissionAmount?: number
  settledCommissionAmount?: number
}

export interface PromoterRelation {
  id?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  bindTime?: string
  firstOrderId?: number
  firstOrderNo?: string
  convertStatus?: string
  createTime?: string
}

export interface PromoterCommissionOrder {
  id?: number
  commissionNo?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  sourceOrderId?: number
  sourceOrderNo?: string
  sourceUnitId?: number
  sourceUnitNo?: string
  commissionType?: string
  commissionAmount?: number
  status?: string
  settleTime?: string
  createTime?: string
}

export const PromoterApi = {
  getPromoterPage: async (params: any) => {
    return await request.get({ url: '/promote/user/page', params })
  },
  getPromoter: async (id: number) => {
    return await request.get<PromoterDetail>({ url: `/promote/user/get?id=${id}` })
  }
}
