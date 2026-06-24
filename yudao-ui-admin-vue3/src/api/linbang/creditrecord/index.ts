import request from '@/config/axios'

/** 信用记录信息 */
export interface CreditRecord {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  merchantId?: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  ruleId?: number
  ruleCode?: string
  ruleName?: string
  scoreChange?: number
  beforeScore?: number
  afterScore?: number
  triggerType?: string
  bizType?: string
  bizId?: number
  bizDisplay?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface CreditRecordDetail extends CreditRecord {
  currentScore?: number
  creditLevel?: string
  sameUserRecordCount?: number
  sameRuleRecordCount?: number
  positiveRecordCount?: number
  negativeRecordCount?: number
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
  }
  merchant?: {
    id?: number
    userId?: number
    merchantName?: string
    contactName?: string
    contactMobile?: string
    status?: string
    acceptStatus?: string
    creditScore?: number
    creditLevel?: string
  }
  rule?: {
    id?: number
    ruleCode?: string
    ruleName?: string
    scoreChange?: number
    triggerType?: string
    status?: string
    createTime?: string
  }
}

export const CreditRecordApi = {
  getCreditRecordPage: async (params: any) => {
    return await request.get({ url: `/review/credit-record/page`, params })
  },

  getCreditRecord: async (id: number) => {
    return await request.get<CreditRecordDetail>({ url: `/review/credit-record/get?id=` + id })
  },

  exportCreditRecord: async (params: any) => {
    return await request.download({ url: `/review/credit-record/export-excel`, params })
  }
}
