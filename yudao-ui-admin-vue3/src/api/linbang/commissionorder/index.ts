import request from '@/config/axios'

export interface CommissionOrder {
  id: number
  commissionNo?: string
  promoterId?: number
  promoterUserNo?: string
  promoterUserNickname?: string
  promoterUserMobile?: string
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

export interface CommissionOrderDetail extends CommissionOrder {
  updateTime?: string
  promoter?: CommissionOrderPromoter
  user?: CommissionOrderUser
  sourceOrder?: CommissionOrderSourceOrder
  sourceUnit?: CommissionOrderSourceUnit
}

export interface CommissionOrderPromoter {
  id?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  levelCode?: string
  inviteCode?: string
  status?: string
  totalCommissionAmount?: number
  availableCommissionAmount?: number
}

export interface CommissionOrderUser {
  id?: number
  userNo?: string
  mobile?: string
  nickname?: string
  currentRoleCode?: string
  status?: string
}

export interface CommissionOrderSourceOrder {
  id?: number
  orderNo?: string
  userId?: number
  merchantId?: number
  categoryId?: number
  title?: string
  orderAmount?: number
  pricingMode?: string
  splitStatus?: string
  status?: string
  createTime?: string
}

export interface CommissionOrderSourceUnit {
  id?: number
  orderId?: number
  unitNo?: string
  unitSeq?: number
  unitTitle?: string
  unitAmount?: number
  splitMode?: string
  isLocked?: boolean
  lockReason?: string
  merchantId?: number
  status?: string
  finishTime?: string
  createTime?: string
}

export const CommissionOrderApi = {
  getCommissionOrderPage: async (params: any) => {
    return await request.get({ url: '/promote/commission/page', params })
  },
  getCommissionOrder: async (id: number) => {
    return await request.get<CommissionOrderDetail>({ url: `/promote/commission/get?id=${id}` })
  }
}
