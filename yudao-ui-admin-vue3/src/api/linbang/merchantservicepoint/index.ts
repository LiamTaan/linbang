import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MerchantServicePoint {
  id?: number
  merchantId?: number
  merchantName?: string
  contactName?: string
  contactMobile?: string
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
  createTime?: string | Dayjs
}

export interface MerchantServicePointDetail extends MerchantServicePoint {
  updateTime?: string | Dayjs
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
    latestEntryId?: number
    latestEntryNo?: string
    latestEntryStatus?: string
    latestRegionCode?: string
  }
  summary?: {
    servicePointCount?: number
    enabledServicePointCount?: number
    disabledServicePointCount?: number
    sameDistrictPointCount?: number
    sameCityPointCount?: number
    categoryCount?: number
  }
  categories?: Array<{
    categoryId?: number
    categoryName?: string
    parentId?: number
    categoryLevel?: number
    defaultPricingMode?: string
    supportSplit?: boolean
    supportInvoice?: boolean
  }>
  relatedPoints?: Array<{
    id?: number
    pointName?: string
    province?: string
    city?: string
    district?: string
    street?: string
    detailAddress?: string
    serviceRadiusKm?: number
    status?: string
    createTime?: string | Dayjs
  }>
}

export const MerchantServicePointApi = {
  getMerchantServicePointPage: async (params: any) => {
    return await request.get({ url: '/merchant/service-point/page', params })
  },

  getMerchantServicePoint: async (id: number) => {
    return await request.get<MerchantServicePointDetail>({ url: `/merchant/service-point/get?id=${id}` })
  },

  createMerchantServicePoint: async (data: MerchantServicePoint) => {
    return await request.post({ url: '/merchant/service-point/create', data })
  },

  updateMerchantServicePoint: async (data: MerchantServicePoint) => {
    return await request.put({ url: '/merchant/service-point/update', data })
  },

  deleteMerchantServicePoint: async (id: number) => {
    return await request.delete({ url: `/merchant/service-point/delete?id=${id}` })
  },

  deleteMerchantServicePointList: async (ids: number[]) => {
    return await request.delete({ url: `/merchant/service-point/delete-list?ids=${ids.join(',')}` })
  },

  exportMerchantServicePoint: async (params: any) => {
    return await request.download({ url: '/merchant/service-point/export-excel', params })
  }
}
