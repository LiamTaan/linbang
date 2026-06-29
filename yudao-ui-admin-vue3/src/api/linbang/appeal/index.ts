import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface Appeal {
  id: number
  appealNo?: string
  orderId?: number
  orderNo?: string
  unitId?: number
  unitNo?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  appealType?: string
  content?: string
  status?: string
  auditStatus?: string
  auditBy?: number
  auditTime?: string | Dayjs
  auditRemark?: string
  rejectReason?: string
  createTime?: string | Dayjs
}

export interface AppealDetail extends Appeal {
  updateTime?: string | Dayjs
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
    orderAmount?: number
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    status?: string
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    acceptDeadlineTime?: string | Dayjs
    finishTime?: string | Dayjs
  }
  user?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
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
    serviceScopeDesc?: string
  }
  summary?: {
    attachmentCount?: number
    sameOrderAppealCount?: number
    sameUserAppealCount?: number
    pendingAuditCount?: number
    processingCount?: number
    approvedCount?: number
    rejectedCount?: number
    finishedCount?: number
  }
  files?: Array<{
    fileId?: number
  }>
  relatedAppeals?: Array<{
    id?: number
    appealNo?: string
    orderId?: number
    unitId?: number
    appealType?: string
    status?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    auditTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  coordinationRecords?: Array<{
    id?: number
    partnerId?: number
    status?: string
    coordinationRemark?: string
    escalateRemark?: string
    initiatedBy?: number
    initiatedTime?: string | Dayjs
    finishedBy?: number
    finishedTime?: string | Dayjs
  }>
  operateLogs?: Array<{
    id?: number
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

export interface AppealAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export const AppealApi = {
  getAppealPage: async (params: any) => {
    return await request.get({ url: `/review/appeal/page`, params })
  },

  getAppeal: async (id: number) => {
    return await request.get<AppealDetail>({ url: `/review/appeal/get?id=${id}` })
  },

  createAppeal: async (data: Appeal) => {
    return await request.post({ url: `/review/appeal/create`, data })
  },

  updateAppeal: async (data: Appeal) => {
    return await request.put({ url: `/review/appeal/update`, data })
  },

  auditAppeal: async (data: AppealAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/review/appeal/audit`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  exportAppeal: async (params: any) => {
    return await request.download({ url: `/review/appeal/export-excel`, params })
  }
}
