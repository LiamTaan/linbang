import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface MerchantPriceReport {
  id: number
  merchantId?: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  partnerId?: number
  partnerName?: string
  categoryId?: number
  categoryName?: string
  regionCode?: string
  suggestedPrice?: number
  remark?: string
  status?: string
  auditStatus?: string
  auditRemark?: string
  rejectReason?: string
  auditBy?: number
  auditTime?: string
  createTime?: string
}

export interface MerchantPriceReportDetail extends MerchantPriceReport {
  updateTime?: string
  merchant?: MerchantPriceReportMerchant
  partner?: MerchantPriceReportPartner
  merchantEntry?: MerchantPriceReportMerchantEntry
  summary?: MerchantPriceReportSummary
  relatedReports?: MerchantPriceReportRelatedReport[]
}

export interface MerchantPriceReportMerchant {
  id?: number
  merchantName?: string
  contactName?: string
  contactMobile?: string
  status?: string
  acceptStatus?: string
  creditScore?: number
  creditLevel?: string
}

export interface MerchantPriceReportPartner {
  id?: number
  partnerName?: string
  contactName?: string
  contactMobile?: string
  status?: string
}

export interface MerchantPriceReportMerchantEntry {
  id?: number
  merchantId?: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  entryNo?: string
  regionCode?: string
  firstAuditStatus?: string
  finalAuditStatus?: string
  status?: string
  remark?: string
  createTime?: string
}

export interface MerchantPriceReportSummary {
  totalRelatedCount?: number
  pendingCount?: number
  approvedCount?: number
  rejectedCount?: number
  avgSuggestedPrice?: number
  minSuggestedPrice?: number
  maxSuggestedPrice?: number
}

export interface MerchantPriceReportRelatedReport {
  id?: number
  merchantId?: number
  merchantName?: string
  partnerId?: number
  partnerName?: string
  categoryId?: number
  categoryName?: string
  regionCode?: string
  suggestedPrice?: number
  remark?: string
  status?: string
  createTime?: string
}

export interface MerchantPriceReportAuditReqVO {
  id: number
  auditStatus: 'APPROVED' | 'REJECTED'
  auditRemark?: string
  rejectReason?: string
}

export const MerchantPriceReportApi = {
  getMerchantPriceReportPage: async (params: any) => {
    return await request.get({ url: '/partner/price-report/page', params })
  },
  getMerchantPriceReport: async (id: number) => {
    return await request.get<MerchantPriceReportDetail>({ url: `/partner/price-report/get?id=${id}` })
  },

  auditMerchantPriceReport: async (data: MerchantPriceReportAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: '/partner/price-report/audit',
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  }
}
