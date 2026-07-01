import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface WalletWithdraw {
  id: number
  withdrawNo?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  walletAccountId?: number
  walletRoleCode?: string
  walletStatus?: string
  bankCardId?: number
  bankName?: string
  cardNoMask?: string
  bankAccountName?: string
  applyAmount?: number
  feeAmount?: number
  realAmount?: number
  status?: string
  auditStatus?: string
  auditRemark?: string
  auditBy?: number
  auditTime?: string | Dayjs
  rejectReason?: string
  payTime?: string | Dayjs
  payTransferId?: number
  payTransferNo?: string
  transferErrorMsg?: string
  createTime?: string | Dayjs
}

export interface WalletWithdrawDetail extends WalletWithdraw {
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
  }
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
  bankCard?: {
    id?: number
    userId?: number
    bankName?: string
    bankCode?: string
    transferAccount?: string
    cardNoMask?: string
    accountName?: string
    bankProvince?: string
    bankCity?: string
    reservedMobile?: string
    status?: string
    isDefault?: boolean
  }
  relatedFlows?: {
    id?: number
    flowNo?: string
    userId?: number
    walletAccountId?: number
    bizType?: string
    flowType?: string
    changeAmount?: number
    beforeAmount?: number
    afterAmount?: number
    relatedPayOrderId?: number
    relatedRefundId?: number
    remark?: string
    createTime?: string | Dayjs
  }[]
}

export interface WithdrawAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export const WalletWithdrawApi = {
  getWalletWithdrawPage: async (params: any) => {
    return await request.get({ url: `/wallet/withdraw/page`, params })
  },

  getWalletWithdraw: async (id: number) => {
    return await request.get<WalletWithdrawDetail>({ url: `/wallet/withdraw/get?id=${id}` })
  },

  createWalletWithdraw: async (data: WalletWithdraw) => {
    return await request.post({ url: `/wallet/withdraw/create`, data })
  },

  updateWalletWithdraw: async (data: WalletWithdraw) => {
    return await request.put({ url: `/wallet/withdraw/update`, data })
  },

  auditWalletWithdraw: async (data: WithdrawAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/wallet/withdraw/audit`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  retryWalletWithdrawTransfer: async (id: number, verifyToken?: string) => {
    return await request.post({
      url: `/wallet/withdraw/retry-transfer?id=${id}`,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  exportWalletWithdraw: async (params: any) => {
    return await request.download({ url: `/wallet/withdraw/export-excel`, params })
  }
}
