import request from '@/config/axios'

/** 钱包账户信息 */
export interface WalletAccount {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  roleCode?: string
  totalAmount?: number
  availableAmount?: number
  frozenAmount?: number
  escrowAmount?: number
  commissionAmount?: number
  status?: string
}

export interface WalletAccountDetail extends WalletAccount {
  createTime?: string
  updateTime?: string
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
  }
  defaultBankCard?: {
    id?: number
    userId?: number
    bankName?: string
    bankCode?: string
    cardNoMask?: string
    accountName?: string
    reservedMobile?: string
    status?: string
    isDefault?: boolean
    createTime?: string
  }
  recentFlows?: {
    id?: number
    flowNo?: string
    userId?: number
    walletAccountId?: number
    bizType?: string
    flowType?: string
    changeAmount?: number
    beforeAmount?: number
    afterAmount?: number
    relatedOrderId?: number
    relatedUnitId?: number
    relatedPayOrderId?: number
    relatedRefundId?: number
    remark?: string
    createTime?: string
  }[]
  recentWithdraws?: {
    id?: number
    withdrawNo?: string
    bankCardId?: number
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

export interface WalletAccountFormData {
  id?: number
  userId?: number
  roleCode?: string
  totalAmount?: number
  availableAmount?: number
  frozenAmount?: number
  escrowAmount?: number
  commissionAmount?: number
  status?: string
}

// 钱包账户 API
export const WalletAccountApi = {
  // 查询钱包账户分页
  getWalletAccountPage: async (params: any) => {
    return await request.get({ url: `/wallet/account/page`, params })
  },

  // 查询钱包账户详情
  getWalletAccount: async (id: number) => {
    return await request.get<WalletAccountDetail>({ url: `/wallet/account/get?id=` + id })
  },

  // 新增钱包账户
  createWalletAccount: async (data: WalletAccountFormData) => {
    return await request.post({ url: `/wallet/account/create`, data })
  },

  // 修改钱包账户
  updateWalletAccount: async (data: WalletAccountFormData) => {
    return await request.put({ url: `/wallet/account/update`, data })
  },

  // 删除钱包账户
  deleteWalletAccount: async (id: number) => {
    return await request.delete({ url: `/wallet/account/delete?id=` + id })
  },

  /** 批量删除钱包账户 */
  deleteWalletAccountList: async (ids: number[]) => {
    return await request.delete({ url: `/wallet/account/delete-list?ids=${ids.join(',')}` })
  },

  // 导出钱包账户 Excel
  exportWalletAccount: async (params: any) => {
    return await request.download({ url: `/wallet/account/export-excel`, params })
  }
}
