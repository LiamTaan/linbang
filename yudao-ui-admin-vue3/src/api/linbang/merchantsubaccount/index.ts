import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface MerchantSubAccount {
  id?: number
  merchantId?: number
  merchantName?: string
  userId?: number
  userNo?: string
  userNickname?: string
  mobile?: string
  permissionCodes?: string[]
  servicePointIds?: number[]
  servicePointNames?: string[]
  status?: string
  remark?: string
  createTime?: string | Dayjs
}

export interface MerchantSubAccountSaveReqVO {
  id?: number
  merchantId: number
  userId: number
  mobile: string
  permissionCodes: string[]
  remark?: string
}

export interface MerchantSubAccountStatusUpdateReqVO {
  id: number
  status: string
}

export interface MerchantSubAccountServicePointUpdateReqVO {
  id: number
  servicePointIds: number[]
}

export const MerchantSubAccountApi = {
  getMerchantSubAccountPage: async (params: any) => {
    return await request.get({ url: '/merchant/sub-account/page', params })
  },

  getMerchantSubAccount: async (id: number) => {
    return await request.get<MerchantSubAccount>({ url: `/merchant/sub-account/get?id=${id}` })
  },

  createMerchantSubAccount: async (data: MerchantSubAccountSaveReqVO) => {
    return await request.post({ url: '/merchant/sub-account/create', data })
  },

  updateMerchantSubAccount: async (data: MerchantSubAccountSaveReqVO) => {
    return await request.put({ url: '/merchant/sub-account/update', data })
  },

  updateMerchantSubAccountStatus: async (data: MerchantSubAccountStatusUpdateReqVO) => {
    return await request.put({ url: '/merchant/sub-account/status/update', data })
  },

  updateMerchantSubAccountServicePoints: async (data: MerchantSubAccountServicePointUpdateReqVO) => {
    return await request.put({ url: '/merchant/sub-account/service-point/update', data })
  }
}
