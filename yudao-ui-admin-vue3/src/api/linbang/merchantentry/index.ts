import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface MerchantEntry {
  id: number
  merchantId?: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  entryNo?: string
  regionCode?: string
  firstAuditStatus?: string
  firstAuditBy?: number
  firstAuditTime?: string | Dayjs
  finalAuditStatus?: string
  finalAuditBy?: number
  finalAuditTime?: string | Dayjs
  status?: string
  remark?: string
  createTime?: string | Dayjs
}

export interface MerchantEntryDetail extends MerchantEntry {
  updateTime?: string | Dayjs
  applicant?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
    lastLoginIp?: string
  }
  merchant?: {
    id?: number
    merchantName?: string
    contactName?: string
    contactMobile?: string
    serviceScopeDesc?: string
    status?: string
    acceptStatus?: string
    creditScore?: number
    creditLevel?: string
  }
  realName?: {
    id?: number
    realName?: string
    idCardNo?: string
    auditStatus?: string
    auditRemark?: string
    auditBy?: number
    auditTime?: string | Dayjs
    rejectReason?: string
  }
  summary?: {
    historyEntryCount?: number
    approvedEntryCount?: number
    rejectedEntryCount?: number
    categoryCount?: number
    qualificationCount?: number
    approvedQualificationCount?: number
  }
  categories?: Array<{
    categoryId?: number
    categoryName?: string
    parentId?: number
    categoryLevel?: number
    defaultPricingMode?: string
    supportSplit?: boolean
    supportInvoice?: boolean
  }>
  qualifications?: Array<{
    id?: number
    qualificationType?: string
    qualificationName?: string
    qualificationNo?: string
    fileId?: number
    validStartDate?: string
    validEndDate?: string
    auditStatus?: string
    auditRemark?: string
    auditBy?: number
    auditTime?: string | Dayjs
    rejectReason?: string
    createTime?: string | Dayjs
  }>
  historyEntries?: Array<{
    id?: number
    entryNo?: string
    regionCode?: string
    firstAuditStatus?: string
    finalAuditStatus?: string
    status?: string
    remark?: string
    createTime?: string | Dayjs
  }>
}

export interface MerchantEntryAuditReqVO {
  id: number
  auditStatus: 'FIRST_APPROVED' | 'APPROVED' | 'REJECTED'
  auditRemark?: string
  rejectReason?: string
}

export const MerchantEntryApi = {
  getMerchantEntryPage: async (params: any) => {
    return await request.get({ url: `/merchant/entry/page`, params })
  },

  getMerchantEntry: async (id: number) => {
    return await request.get<MerchantEntryDetail>({ url: `/merchant/entry/get?id=${id}` })
  },

  createMerchantEntry: async (data: MerchantEntry) => {
    return await request.post({ url: `/merchant/entry/create`, data })
  },

  updateMerchantEntry: async (data: MerchantEntry) => {
    return await request.put({ url: `/merchant/entry/update`, data })
  },

  auditMerchantEntry: async (data: MerchantEntryAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/merchant/entry/audit`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  exportMerchantEntry: async (params: any) => {
    return await request.download({ url: `/merchant/entry/export-excel`, params })
  }
}
