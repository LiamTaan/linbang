import request from '@/config/axios'

export interface PartnerInfo {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  partnerName?: string
  contactName?: string
  contactMobile?: string
  status?: string
  createTime?: string
  regionAdcodes?: string[]
}

export interface PartnerInfoDetail extends PartnerInfo {
  updateTime?: string
  regions?: PartnerRegion[]
  summary?: PartnerSummary
  recentPriceReports?: PartnerRecentPriceReport[]
}

export interface PartnerRegion {
  id?: number
  province?: string
  city?: string
  district?: string
  adcode?: string
  status?: string
  createTime?: string
}

export interface PartnerSummary {
  regionCount?: number
  enabledRegionCount?: number
  pendingEntryAuditCount?: number
  pendingComplaintCount?: number
  pendingPriceReportCount?: number
  orderCount?: number
  tradeAmount?: number
  approvedPriceReportCount?: number
  rejectedPriceReportCount?: number
}

export interface PartnerRecentPriceReport {
  id?: number
  merchantId?: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  partnerId?: number
  categoryId?: number
  categoryName?: string
  regionCode?: string
  suggestedPrice?: number
  remark?: string
  status?: string
  createTime?: string
}

export const PartnerInfoApi = {
  getPartnerInfoPage: async (params: any) => {
    return await request.get({ url: '/partner/info/page', params })
  },
  getPartnerInfo: async (id: number) => {
    return await request.get<PartnerInfoDetail>({ url: `/partner/info/get?id=${id}` })
  }
}
