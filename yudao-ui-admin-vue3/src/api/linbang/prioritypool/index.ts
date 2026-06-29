import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface PriorityPoolRecord {
  id: number
  merchantId?: number
  userId?: number
  status?: string
  reasonCode?: string
  reasonRemark?: string
  currentFlag?: boolean
  effectiveTime?: string | Dayjs
  expireTime?: string | Dayjs
}

export interface PriorityPoolFreezeReqVO {
  merchantId: number
  reasonRemark: string
}

export const PriorityPoolApi = {
  getPriorityPoolPage: async (params: any) => {
    return await request.get({ url: '/linbang/priority-pool/page', params })
  },
  getPriorityPool: async (id: number) => {
    return await request.get<PriorityPoolRecord>({ url: `/linbang/priority-pool/get?id=${id}` })
  },
  freezePriorityPool: async (data: PriorityPoolFreezeReqVO) => {
    return await request.post({ url: '/linbang/priority-pool/freeze', data })
  },
  unfreezePriorityPool: async (data: PriorityPoolFreezeReqVO) => {
    return await request.post({ url: '/linbang/priority-pool/unfreeze', data })
  }
}
