import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

/** 异常订单信息 */
export interface OrderAbnormal {
  id: number
  orderId?: number
  orderNo?: string
  unitId?: number
  unitNo?: string
  abnormalNo?: string
  abnormalType?: string
  riskLevel?: string
  hitRuleCode: string
  handleStatus?: string
  handleBy: number
  handleTime?: string | Dayjs
  remark: string
  finalAuditStatus?: string
  finalAuditBy?: number
  finalAuditTime?: string | Dayjs
  finalAuditRemark?: string
  createTime?: string | Dayjs
}

export interface OrderAbnormalFinalAuditReqVO {
  id: number
  finalAuditStatus: 'APPROVED' | 'REJECTED'
  finalAuditRemark: string
}

// 异常订单 API
export const OrderAbnormalApi = {
  // 查询异常订单分页
  getOrderAbnormalPage: async (params: any) => {
    return await request.get({ url: `/order/abnormal/page`, params })
  },

  // 查询异常订单详情
  getOrderAbnormal: async (id: number) => {
    return await request.get<OrderAbnormal>({ url: `/order/abnormal/get?id=` + id })
  },

  // 新增异常订单
  createOrderAbnormal: async (data: OrderAbnormal) => {
    return await request.post({ url: `/order/abnormal/create`, data })
  },

  // 修改异常订单
  updateOrderAbnormal: async (data: OrderAbnormal) => {
    return await request.put({ url: `/order/abnormal/update`, data })
  },

  // 删除异常订单
  deleteOrderAbnormal: async (id: number) => {
    return await request.delete({ url: `/order/abnormal/delete?id=` + id })
  },

  /** 批量删除异常订单 */
  deleteOrderAbnormalList: async (ids: number[]) => {
    return await request.delete({ url: `/order/abnormal/delete-list?ids=${ids.join(',')}` })
  },

  // 导出异常订单 Excel
  exportOrderAbnormal: async (params) => {
    return await request.download({ url: `/order/abnormal/export-excel`, params })
  },

  // 异常单终审
  finalAuditOrderAbnormal: async (data: OrderAbnormalFinalAuditReqVO, verifyToken?: string) => {
    return await request.post({
      url: `/order/abnormal/final-audit`,
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  }
}
