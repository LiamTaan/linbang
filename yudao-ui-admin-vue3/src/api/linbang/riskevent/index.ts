import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface RiskEvent {
  id: number
  bizType?: string
  bizId?: number
  bizDisplay?: string
  riskType?: string
  riskLevel?: string
  hitRuleCode?: string
  status?: string
  handleBy?: number
  handleTime?: string
  remark?: string
  createTime?: string | Dayjs
}

export interface RiskEventDetail extends RiskEvent {
  hitRule?: {
    id?: number
    ruleCode?: string
    ruleName?: string
    ruleGroup?: string
    ruleValue?: string
    valueType?: string
    status?: string
    remark?: string
  }
  order?: {
    id?: number
    orderNo?: string
    title?: string
    status?: string
    userId?: number
    userNo?: string
    userNickname?: string
    userMobile?: string
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    status?: string
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
  }
  abnormal?: {
    id?: number
    orderId?: number
    unitId?: number
    abnormalNo?: string
    abnormalType?: string
    riskLevel?: string
    handleStatus?: string
    handleBy?: number
    handleTime?: string | Dayjs
    remark?: string
    createTime?: string | Dayjs
  }
  complaint?: {
    id?: number
    complaintNo?: string
    orderId?: number
    unitId?: number
    complainantUserId?: number
    respondentUserId?: number
    complaintType?: string
    status?: string
    handleTime?: string | Dayjs
    resultDesc?: string
    createTime?: string | Dayjs
  }
  appeal?: {
    id?: number
    appealNo?: string
    orderId?: number
    unitId?: number
    userId?: number
    appealType?: string
    status?: string
    auditStatus?: string
    auditBy?: number
    auditTime?: string | Dayjs
    auditRemark?: string
    rejectReason?: string
    createTime?: string | Dayjs
  }
  withdraw?: {
    id?: number
    withdrawNo?: string
    userId?: number
    walletAccountId?: number
    bankCardId?: number
    applyAmount?: number
    feeAmount?: number
    realAmount?: number
    status?: string
    auditStatus?: string
    auditBy?: number
    auditTime?: string | Dayjs
    auditRemark?: string
    rejectReason?: string
    payTime?: string | Dayjs
    createTime?: string | Dayjs
  }
  orderOperateLogs?: Array<{
    id?: number
    orderId?: number
    unitId?: number
    operateType?: string
    operateRole?: string
    operateBy?: number
    beforeStatus?: string
    afterStatus?: string
    remark?: string
    operateTime?: string | Dayjs
  }>
}

export const RiskEventApi = {
  getRiskEventPage: async (params: any) => {
    return await request.get({ url: '/risk/event/page', params })
  },

  getRiskEvent: async (id: number) => {
    return await request.get<RiskEventDetail>({ url: `/risk/event/get?id=${id}` })
  }
}
