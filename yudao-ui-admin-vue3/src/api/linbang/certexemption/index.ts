import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface CertExemption {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  merchantId?: number
  merchantName?: string
  qualificationId?: number
  qualificationName?: string
  exemptionType?: string
  reason?: string
  auditStatus?: string
  auditRemark?: string
  rejectReason?: string
  effectiveStartTime?: string | Dayjs
  effectiveEndTime?: string | Dayjs
  createTime?: string | Dayjs
}

export interface CertExemptionDetail extends CertExemption {
  attachmentFileIdsJson?: string
  auditBy?: number
  auditTime?: string | Dayjs
  user?: {
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
  qualification?: {
    id?: number
    qualificationType?: string
    qualificationName?: string
    qualificationNo?: string
    fileId?: number
    validStartDate?: string | Dayjs
    validEndDate?: string | Dayjs
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    priorityEnabled?: boolean
  }
  relatedApplies?: Array<{
    id?: number
    exemptionType?: string
    qualificationId?: number
    auditStatus?: string
    effectiveStartTime?: string | Dayjs
    effectiveEndTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
}

export interface CertExemptionAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export const CertExemptionApi = {
  getCertExemptionPage: async (params: any) => {
    return await request.get({ url: '/member/cert-exemption/page', params })
  },

  getCertExemption: async (id: number) => {
    return await request.get<CertExemptionDetail>({ url: `/member/cert-exemption/get?id=${id}` })
  },

  auditCertExemption: async (data: CertExemptionAuditReqVO) => {
    return await request.post({ url: '/member/cert-exemption/audit', data })
  }
}
