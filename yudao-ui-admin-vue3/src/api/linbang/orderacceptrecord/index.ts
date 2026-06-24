import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 抢单记录信息 */
export interface OrderAcceptRecord {
          id: number; // 主键
          orderId?: number; // 订单ID
          orderNo?: string
          unitId?: number; // 单元ID
          unitNo?: string
          merchantId?: number; // 服务商ID
          merchantName?: string
          merchantContactName?: string
          merchantContactMobile?: string
          acceptTime?: string | Dayjs; // 接单时间
          distanceKm: number; // 距离公里
          acceptResult?: string; // 接单结果
          remark: string; // 备注
  }

export interface OrderAcceptRecordDetail extends OrderAcceptRecord {
  order?: {
    id?: number
    orderNo?: string
    title?: string
    status?: string
    userId?: number
    userNo?: string
    userNickname?: string
    userMobile?: string
    merchantId?: number
    orderAmount?: number
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    status?: string
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    acceptDeadlineTime?: string | Dayjs
  }
  merchant?: {
    id?: number
    merchantName?: string
    contactName?: string
    contactMobile?: string
    status?: string
    acceptStatus?: string
    creditScore?: number
    creditLevel?: string
  }
  summary?: {
    matchRecordCount?: number
    pushedCount?: number
    acceptedCount?: number
    rejectedCount?: number
    highestMatchScore?: number
  }
  matchRecords?: Array<{
    id?: number
    orderId?: number
    unitId?: number
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
    matchRuleCode?: string
    matchScore?: number
    distanceKm?: number
    pushTime?: string | Dayjs
    acceptDeadlineTime?: string | Dayjs
    status?: string
  }>
}

// 抢单记录 API
export const OrderAcceptRecordApi = {
  // 查询抢单记录分页
  getOrderAcceptRecordPage: async (params: any) => {
    return await request.get({ url: `/order/accept-record/page`, params })
  },

  // 查询抢单记录详情
  getOrderAcceptRecord: async (id: number) => {
    return await request.get<OrderAcceptRecordDetail>({ url: `/order/accept-record/get?id=` + id })
  },

  // 新增抢单记录
  createOrderAcceptRecord: async (data: OrderAcceptRecord) => {
    return await request.post({ url: `/order/accept-record/create`, data })
  },

  // 修改抢单记录
  updateOrderAcceptRecord: async (data: OrderAcceptRecord) => {
    return await request.put({ url: `/order/accept-record/update`, data })
  },

  // 删除抢单记录
  deleteOrderAcceptRecord: async (id: number) => {
    return await request.delete({ url: `/order/accept-record/delete?id=` + id })
  },

  /** 批量删除抢单记录 */
  deleteOrderAcceptRecordList: async (ids: number[]) => {
    return await request.delete({ url: `/order/accept-record/delete-list?ids=${ids.join(',')}` })
  },

  // 导出抢单记录 Excel
  exportOrderAcceptRecord: async (params) => {
    return await request.download({ url: `/order/accept-record/export-excel`, params })
  }
}
