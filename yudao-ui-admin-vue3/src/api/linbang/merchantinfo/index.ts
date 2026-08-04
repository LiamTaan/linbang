import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

/** 服务商信息表信息 */
export interface MerchantInfo {
  id: number
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  merchantName?: string
  contactName?: string
  contactMobile?: string
  serviceScopeDesc: string
  status?: string
  acceptStatus?: string
  dispatchEnabled?: boolean
  creditScore?: number
  creditLevel: string
  compositeScore?: number
  positiveRate?: number
  inPositivePriorityPool?: boolean
  createTime?: string | Dayjs
}

export interface MerchantInfoDetail extends MerchantInfo {
  entryId?: number
  entryNo?: string
  regionCode?: string
  entryStatus?: string
  firstAuditStatus?: string
  finalAuditStatus?: string
  servicePointCount?: number
  enabledServicePointCount?: number
  updateTime?: string | Dayjs
  categories?: Array<{
    categoryId?: number
    categoryName?: string
    parentId?: number
    categoryLevel?: number
    defaultPricingMode?: string
    supportSplit?: boolean
    supportInvoice?: boolean
  }>
  servicePoints?: Array<{
    servicePointId?: number
    pointName?: string
    province?: string
    city?: string
    district?: string
    street?: string
    detailAddress?: string
    longitude?: number
    latitude?: number
    serviceRadiusKm?: number
    status?: string
  }>
}

// 服务商信息表 API
export const MerchantInfoApi = {
  // 查询服务商信息表分页
  getMerchantInfoPage: async (params: any) => {
    return await request.get({ url: `/linbang/merchant-info/page`, params })
  },

  // 查询服务商信息表详情
  getMerchantInfo: async (id: number) => {
    return await request.get<MerchantInfoDetail>({ url: `/linbang/merchant-info/get?id=` + id })
  },

  // 导出服务商信息表 Excel
  exportMerchantInfo: async (params) => {
    return await request.download({ url: `/linbang/merchant-info/export-excel`, params })
  }
}
