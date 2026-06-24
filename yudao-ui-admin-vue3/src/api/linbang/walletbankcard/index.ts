import request from '@/config/axios'

/** 用户银行卡信息 */
export interface WalletBankCard {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  bankName?: string
  bankCode?: string
  cardNoEncrypt?: string
  cardNoMask?: string
  accountName?: string
  reservedMobile?: string
  status?: string
  isDefault?: boolean
  createTime?: string
}

export interface WalletBankCardDetail extends WalletBankCard {
  updateTime?: string
  userNo?: string
  userNickname?: string
  userMobile?: string
  walletAccounts?: {
    id?: number
    userId?: number
    roleCode?: string
    totalAmount?: number
    availableAmount?: number
    frozenAmount?: number
    escrowAmount?: number
    commissionAmount?: number
    status?: string
  }[]
  recentWithdraws?: {
    id?: number
    withdrawNo?: string
    walletAccountId?: number
    applyAmount?: number
    feeAmount?: number
    realAmount?: number
    status?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    payTime?: string
    createTime?: string
  }[]
  withdrawStats?: {
    totalCount?: number
    totalApplyAmount?: number
    pendingCount?: number
    pendingAmount?: number
    successCount?: number
    successAmount?: number
  }
}

export interface WalletBankCardFormData {
  id?: number
  userId?: number
  bankName?: string
  bankCode?: string
  cardNoEncrypt?: string
  cardNoMask?: string
  accountName?: string
  reservedMobile?: string
  status?: string
  isDefault?: boolean
}

// 用户银行卡 API
export const WalletBankCardApi = {
  // 查询用户银行卡分页
  getWalletBankCardPage: async (params: any) => {
    return await request.get({ url: `/wallet/bank-card/page`, params })
  },

  // 查询用户银行卡详情
  getWalletBankCard: async (id: number) => {
    return await request.get<WalletBankCardDetail>({ url: `/wallet/bank-card/get?id=` + id })
  },

  // 新增用户银行卡
  createWalletBankCard: async (data: WalletBankCardFormData) => {
    return await request.post({ url: `/wallet/bank-card/create`, data })
  },

  // 修改用户银行卡
  updateWalletBankCard: async (data: WalletBankCardFormData) => {
    return await request.put({ url: `/wallet/bank-card/update`, data })
  },

  // 删除用户银行卡
  deleteWalletBankCard: async (id: number) => {
    return await request.delete({ url: `/wallet/bank-card/delete?id=` + id })
  },

  /** 批量删除用户银行卡 */
  deleteWalletBankCardList: async (ids: number[]) => {
    return await request.delete({ url: `/wallet/bank-card/delete-list?ids=${ids.join(',')}` })
  },

  // 导出用户银行卡 Excel
  exportWalletBankCard: async (params: any) => {
    return await request.download({ url: `/wallet/bank-card/export-excel`, params })
  }
}
