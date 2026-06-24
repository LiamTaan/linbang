import request from '@/config/axios'

/** 钱包流水信息 */
export interface WalletFlow {
  id: number
  flowNo?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  walletAccountId?: number
  walletRoleCode?: string
  walletStatus?: string
  walletAvailableAmount?: number
  bizType?: string
  flowType?: string
  changeAmount?: number
  beforeAmount?: number
  afterAmount?: number
  relatedOrderId?: number
  relatedOrderNo?: string
  relatedUnitId?: number
  relatedUnitNo?: string
  relatedPayOrderId?: number
  relatedRefundId?: number
  remark?: string
  createTime?: string
}

export interface WalletFlowDetail extends WalletFlow {
  userNo?: string
  userNickname?: string
  userMobile?: string
  walletAccount?: {
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
  order?: {
    id?: number
    orderNo?: string
    userId?: number
    merchantId?: number
    title?: string
    pricingMode?: string
    orderAmount?: number
    splitStatus?: string
    status?: string
    payOrderId?: number
    createTime?: string
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
    status?: string
    createTime?: string
  }
  payOrder?: {
    id?: number
    merchantOrderId?: string
    subject?: string
    price?: number
    status?: number
    channelCode?: string
    channelOrderNo?: string
    no?: string
    refundPrice?: number
    successTime?: string
    createTime?: string
  }
  refund?: {
    id?: number
    merchantRefundId?: string
    merchantOrderId?: string
    status?: number
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    refundPrice?: number
    reason?: string
    successTime?: string
    createTime?: string
  }
}

export interface WalletFlowFormData {
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
}

// 钱包流水 API
export const WalletFlowApi = {
  // 查询钱包流水分页
  getWalletFlowPage: async (params: any) => {
    return await request.get({ url: `/wallet/flow/page`, params })
  },

  // 查询钱包流水详情
  getWalletFlow: async (id: number) => {
    return await request.get<WalletFlowDetail>({ url: `/wallet/flow/get?id=` + id })
  },

  // 新增钱包流水
  createWalletFlow: async (data: WalletFlowFormData) => {
    return await request.post({ url: `/wallet/flow/create`, data })
  },

  // 修改钱包流水
  updateWalletFlow: async (data: WalletFlowFormData) => {
    return await request.put({ url: `/wallet/flow/update`, data })
  },

  // 删除钱包流水
  deleteWalletFlow: async (id: number) => {
    return await request.delete({ url: `/wallet/flow/delete?id=` + id })
  },

  /** 批量删除钱包流水 */
  deleteWalletFlowList: async (ids: number[]) => {
    return await request.delete({ url: `/wallet/flow/delete-list?ids=${ids.join(',')}` })
  },

  // 导出钱包流水 Excel
  exportWalletFlow: async (params: any) => {
    return await request.download({ url: `/wallet/flow/export-excel`, params })
  }
}
