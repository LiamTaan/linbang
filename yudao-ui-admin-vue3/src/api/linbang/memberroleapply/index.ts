import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface MemberRoleApply {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  applyRoleCode?: string
  applyReason?: string
  resourceDesc?: string
  expectedConversionDesc?: string
  abilityDesc?: string
  availableTimeDesc?: string
  auditStatus?: string
  auditRemark?: string
  rejectReason?: string
  auditBy?: number
  auditTime?: string | Dayjs
  createTime?: string | Dayjs
}

export interface MemberRoleApplyDetail extends MemberRoleApply {
  updateTime?: string | Dayjs
  user?: {
    id?: number
    userNo?: string
    nickname?: string
    mobile?: string
    currentRoleCode?: string
    status?: string
  }
  realName?: {
    id?: number
    realName?: string
    idCardNo?: string
    auditStatus?: string
  }
  latestQualification?: {
    id?: number
    qualificationType?: string
    qualificationName?: string
    validEndDate?: string | Dayjs
    auditStatus?: string
  }
  promoter?: {
    id?: number
    inviteCode?: string
    levelCode?: string
    status?: string
  }
}

export interface MemberRoleApplyAuditReqVO {
  id: number
  auditStatus: 'APPROVED' | 'REJECTED'
  auditRemark?: string
  rejectReason?: string
}

export const MemberRoleApplyApi = {
  getMemberRoleApplyPage: async (params: any) => {
    return await request.get({ url: '/member/role-apply/page', params })
  },

  getMemberRoleApply: async (id: number) => {
    return await request.get<MemberRoleApplyDetail>({ url: `/member/role-apply/get?id=${id}` })
  },

  auditMemberRoleApply: async (data: MemberRoleApplyAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: '/member/role-apply/audit',
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  }
}
