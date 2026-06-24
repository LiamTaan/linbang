import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface Complaint {
  id: number
  complaintNo?: string
  orderId?: number
  orderNo?: string
  unitId?: number
  unitNo?: string
  complainantUserId?: number
  complainantUserNo?: string
  complainantUserNickname?: string
  complainantUserMobile?: string
  respondentUserId?: number
  respondentUserNo?: string
  respondentUserNickname?: string
  respondentUserMobile?: string
  complaintType?: string
  content?: string
  status?: string
  handleBy?: number
  handleTime?: string | Dayjs
  resultDesc?: string
  createTime?: string | Dayjs
}

export interface ComplaintDetail extends Complaint {
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
  complainantUser?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
  }
  respondentUser?: {
    id?: number
    userNo?: string
    mobile?: string
    nickname?: string
    currentRoleCode?: string
    status?: string
    lastLoginTime?: string | Dayjs
  }
  respondentMerchant?: {
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
    sameOrderComplaintCount?: number
    sameRespondentComplaintCount?: number
    pendingCount?: number
    processingCount?: number
    finishedCount?: number
    rejectedCount?: number
  }
  files?: Array<{
    fileId?: number
  }>
  relatedComplaints?: Array<{
    id?: number
    complaintNo?: string
    orderId?: number
    unitId?: number
    complaintType?: string
    status?: string
    resultDesc?: string
    handleTime?: string | Dayjs
    createTime?: string | Dayjs
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

export interface ComplaintProcessReqVO {
  id: number
  status: string
  resultDesc?: string
}

export const ComplaintApi = {
  getComplaintPage: async (params: any) => {
    return await request.get({ url: `/review/complaint/page`, params })
  },

  getComplaint: async (id: number) => {
    return await request.get<ComplaintDetail>({ url: `/review/complaint/get?id=${id}` })
  },

  createComplaint: async (data: Complaint) => {
    return await request.post({ url: `/review/complaint/create`, data })
  },

  updateComplaint: async (data: Complaint) => {
    return await request.put({ url: `/review/complaint/update`, data })
  },

  processComplaint: async (data: ComplaintProcessReqVO) => {
    return await request.post({ url: `/review/complaint/process`, data })
  },

  exportComplaint: async (params: any) => {
    return await request.download({ url: `/review/complaint/export-excel`, params })
  }
}
