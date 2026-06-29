import request from '@/config/axios'

export interface OrderSplitRule {
  id?: number
  ruleName?: string
  ruleCode?: string
  matchMode?: string
  categoryId?: number
  categoryName?: string
  applicablePricingModes?: string[]
  minOrderAmount?: number
  minQuantity?: number
  minWorkerCount?: number
  splitMode?: string
  defaultUnitCount?: number
  unitAmountLimit?: number
  unitTemplate?: Record<string, string>
  sortNo?: number
  status?: string
  remark?: string
  createTime?: string
}

export const OrderSplitRuleApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/order/split-rule/page', params })
  },

  get: async (id: number) => {
    return await request.get<OrderSplitRule>({ url: '/order/split-rule/get?id=' + id })
  },

  create: async (data: OrderSplitRule) => {
    return await request.post({ url: '/order/split-rule/create', data })
  },

  update: async (data: OrderSplitRule) => {
    return await request.put({ url: '/order/split-rule/update', data })
  },

  delete: async (id: number) => {
    return await request.delete({ url: '/order/split-rule/delete?id=' + id })
  }
}
