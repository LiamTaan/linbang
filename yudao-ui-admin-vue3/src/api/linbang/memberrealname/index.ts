import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MemberUserRealName {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  realName?: string
  idCardNo?: string
  idCardFrontFileId?: number
  idCardBackFileId?: number
  holdCardFileId?: number
  livenessResult?: string
  faceVerifyResult?: string
  auditStatus?: string
  auditRemark?: string
  auditBy?: number
  auditTime?: string | Dayjs
  rejectReason?: string
  createTime?: string | Dayjs
}

export interface MemberUserRealNameDetail extends MemberUserRealName {
  holdCardVideoFileId?: number
  idCardValidFrom?: string | Dayjs
  idCardValidEnd?: string | Dayjs
  wechatRealNameStatus?: string
  alipayRealNameStatus?: string
  verifyProvider?: string
  verifyFlowNo?: string
  verifyStartedTime?: string | Dayjs
  verifyCompletedTime?: string | Dayjs
  verifyFailReason?: string
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
    qualificationCount?: number
    approvedQualificationCount?: number
    rejectedQualificationCount?: number
    creditRecordCount?: number
    latestCreditScore?: number
    latestCreditLevel?: string
    merchantBound?: boolean
    latestEntryApproved?: boolean
    holdCardVideoUploaded?: boolean
    wechatMatched?: boolean
    alipayMatched?: boolean
  }
  qualifications?: Array<{
    id?: number
    qualificationType?: string
    qualificationName?: string
    qualificationNo?: string
    fileId?: number
    validStartDate?: string | Dayjs
    validEndDate?: string | Dayjs
    auditStatus?: string
    auditRemark?: string
    auditBy?: number
    auditTime?: string | Dayjs
    rejectReason?: string
    createTime?: string | Dayjs
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

export interface MemberUserRealNameAuditReqVO {
  id: number
  auditStatus: string
  auditRemark?: string
  rejectReason?: string
}

export const MemberUserRealNameApi = {
  getMemberUserRealNamePage: async (params: any) => {
    return await request.get({ url: `/member/real-name/page`, params })
  },

  getMemberUserRealName: async (id: number) => {
    return await request.get<MemberUserRealNameDetail>({ url: `/member/real-name/get?id=${id}` })
  },

  createMemberUserRealName: async (data: MemberUserRealName) => {
    return await request.post({ url: `/member/real-name/create`, data })
  },

  updateMemberUserRealName: async (data: MemberUserRealName) => {
    return await request.put({ url: `/member/real-name/update`, data })
  },

  auditMemberUserRealName: async (data: MemberUserRealNameAuditReqVO) => {
    return await request.post({ url: `/member/real-name/audit`, data })
  },

  exportMemberUserRealName: async (params: any) => {
    return await request.download({ url: `/member/real-name/export-excel`, params })
  }
}
