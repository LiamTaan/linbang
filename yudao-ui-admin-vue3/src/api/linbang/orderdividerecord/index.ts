import request from '@/config/axios'

export interface OrderDivideRecord {
  id: number
  divideNo?: string
  orderId?: number
  unitId?: number
  divideRuleId?: number
  targetType?: string
  targetBizId?: number
  divideRate?: number
  divideAmount?: number
  taxAmount?: number
  settleStatus?: string
  cityLevel?: string
  categoryId?: number
  remark?: string
  createTime?: string
}

export const OrderDivideRecordApi = {
  getOrderDivideRecordPage: async (params: any) => {
    return await request.get({ url: `/wallet/divide-record/page`, params })
  },

  getOrderDivideRecord: async (id: number) => {
    return await request.get<OrderDivideRecord>({ url: `/wallet/divide-record/get?id=${id}` })
  }
}
