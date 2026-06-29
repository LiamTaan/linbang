import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MemberQualification {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  qualificationType?: string
  qualificationName?: string
  qualificationNo?: string
  fileId?: number
  evidenceFileIdsJson?: string
  videoFileId?: number
  validStartDate?: string | Dayjs
  validEndDate?: string | Dayjs
  auditStatus?: string
  auditRemark?: string
  auditBy?: number
  auditTime?: string | Dayjs
  rejectReason?: string
  priorityEnabled?: boolean
  createTime?: string | Dayjs
}

export interface MemberQualificationDetail extends MemberQualification {
  updateTime?: string | Dayjs
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
  latestEntry?: {
    id?: number
    entryNo?: string
    regionCode?: string
    firstAuditStatus?: string
    finalAuditStatus?: string
    status?: string
    remark?: string
    createTime?: string | Dayjs
  }
  summary?: {
    sameUserQualificationCount?: number
    approvedQualificationCount?: number
    rejectedQualificationCount?: number
    expiringSoonCount?: number
    creditRecordCount?: number
    latestCreditScore?: number
    latestCreditLevel?: string
    realNameApproved?: boolean
    merchantBound?: boolean
    approvedExemptionCount?: number
  }
  relatedQualifications?: Array<{
    id?: number
    qualificationType?: string
    qualificationName?: string
    qualificationNo?: string
    fileId?: number
    evidenceFileIdsJson?: string
    videoFileId?: number
    validStartDate?: string | Dayjs
    validEndDate?: string | Dayjs
    auditStatus?: string
    auditRemark?: string
    auditBy?: number
    auditTime?: string | Dayjs
    rejectReason?: string
    createTime?: string | Dayjs
  }>
  certExemptions?: Array<{
    id?: number
    exemptionType?: string
    reason?: string
    auditStatus?: string
    effectiveStartTime?: string | Dayjs
    effectiveEndTime?: string | Dayjs
    rejectReason?: string
  }>
  creditRecords?: Array<{
    id?: number
    ruleCode?: string
    ruleName?: string
    scoreChange?: number
    beforeScore?: number
    afterScore?: number
    triggerType?: string
    bizType?: string
    bizId?: number
    remark?: string
    createTime?: string | Dayjs
  }>
}

export interface MemberQualificationAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export const MemberQualificationApi = {
  getMemberQualificationPage: async (params: any) => {
    return await request.get({ url: `/member/qualification/page`, params })
  },

  getMemberQualification: async (id: number) => {
    return await request.get<MemberQualificationDetail>({ url: `/member/qualification/get?id=${id}` })
  },

  auditMemberQualification: async (data: MemberQualificationAuditReqVO) => {
    return await request.post({ url: `/member/qualification/audit`, data })
  }
}
