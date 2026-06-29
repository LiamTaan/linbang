import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

export interface OrderFlowRecord {
  unitId: number
  orderId?: number
  unitAmount?: number
  dispatchStatus?: string
  currentBatchNo?: number
  flowTime?: string | Dayjs
  flowReason?: string
  autoRefundStatus?: string
  autoRefundId?: number
}

export const OrderFlowApi = {
  getOrderFlowPage: async (params: any) => {
    return await request.get({ url: '/linbang/order/flow/page', params })
  },
  retryRefund: async (unitId: number) => {
    return await request.post({ url: '/linbang/order/flow/retry-refund', data: { unitId } })
  }
}
