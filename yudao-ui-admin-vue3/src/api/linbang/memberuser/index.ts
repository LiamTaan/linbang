import request from '@/config/axios'
import type { Dayjs } from 'dayjs';
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

/** 用户主表信息 */
export interface MemberUser {
          id: number; // 主键
          userNo?: string; // 用户编号
          mobile?: string; // 手机号
          nickname?: string; // 昵称
          avatar?: string; // 头像
          gender?: number; // 性别
          birthday?: string | Dayjs; // 生日
          registerSource?: string; // 注册来源
          currentRoleCode?: string; // 当前角色编码
          enabledRoleCodes?: string[]; // 已开通角色编码列表
          status?: string; // 状态
          lastLoginTime?: string | Dayjs; // 最后登录时间
          lastLoginIp?: string; // 最后登录IP
          pointBalance?: number; // 当前积分余额
          remark?: string; // 备注
          createTime?: string | Dayjs
  }

export interface MemberUserDetail extends MemberUser {
  updateTime?: string | Dayjs
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
    merchantId?: number
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
    addressCount?: number
    defaultAddressCount?: number
    creditRecordCount?: number
    latestCreditScore?: number
    latestCreditLevel?: string
    realNameApproved?: boolean
    merchantBound?: boolean
    latestEntryApproved?: boolean
  }
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
  addresses?: Array<{
    id?: number
    receiverName?: string
    receiverMobile?: string
    province?: string
    city?: string
    district?: string
    street?: string
    detailAddress?: string
    longitude?: number
    latitude?: number
    adcode?: string
    isDefault?: boolean
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

export interface MemberUserRestrictReqVO {
  userId: number
  actionType: 'RESTRICT' | 'BAN' | 'BLACKLIST'
  restrictType: string
  reason: string
  endTime?: string | Dayjs
}

export interface MemberUserReleaseRestrictReqVO {
  restrictRecordId: number
  releaseRemark: string
}

// 用户主表 API
export const MemberUserApi = {
  // 查询用户主表分页
  getMemberUserPage: async (params: any) => {
    return await request.get({ url: `/linbang/member-user/page`, params })
  },

  // 查询用户主表详情
  getMemberUser: async (id: number) => {
    return await request.get<MemberUserDetail>({ url: `/linbang/member-user/get?id=` + id })
  },

  // 新增用户主表
  createMemberUser: async (data: MemberUser) => {
    return await request.post({ url: `/linbang/member-user/create`, data })
  },

  // 修改用户主表
  updateMemberUser: async (data: MemberUser) => {
    return await request.put({ url: `/linbang/member-user/update`, data })
  },

  // 删除用户主表
  deleteMemberUser: async (id: number) => {
    return await request.delete({ url: `/linbang/member-user/delete?id=` + id })
  },

  /** 批量删除用户主表 */
  deleteMemberUserList: async (ids: number[]) => {
    return await request.delete({ url: `/linbang/member-user/delete-list?ids=${ids.join(',')}` })
  },

  // 导出用户主表 Excel
  exportMemberUser: async (params) => {
    return await request.download({ url: `/linbang/member-user/export-excel`, params })
  },

  // 限制/封禁/拉黑用户
  restrictMemberUser: async (data: MemberUserRestrictReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/linbang/member-user/restrict`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  // 解除限制/封禁
  releaseMemberUserRestrict: async (data: MemberUserReleaseRestrictReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/linbang/member-user/restrict/release`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  }
}
