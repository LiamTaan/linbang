import request from '@/config/axios'
import type { Dayjs } from 'dayjs';

/** 订单匹配记录信息 */
export interface OrderMatchRecord {
          id: number; // 主键
          orderId?: number; // 订单ID
          orderNo?: string
          unitId?: number; // 单元ID
          unitNo?: string
          merchantId?: number; // 服务商ID
          merchantName?: string
          merchantContactName?: string
          merchantContactMobile?: string
          matchRuleCode: string; // 命中规则编码
          matchScore: number; // 匹配分值
          distanceKm: number; // 距离公里
          stageNo?: number
          pushBatchNo?: number
          priorityLayer?: string
          priorityPoolFlag?: boolean
          categoryMatchLevel?: string
          pushTime?: string | Dayjs; // 推送时间
          acceptDeadlineTime: string | Dayjs; // 接单截止时间
          expiredTime?: string | Dayjs
          status?: string; // 状态
          finalResult?: string
  }

export interface OrderMatchRecordDetail extends OrderMatchRecord {
  createTime?: string | Dayjs
  updateTime?: string | Dayjs
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
  rule?: {
    id?: number
    ruleCode?: string
    ruleName?: string
    ruleGroup?: string
    ruleValue?: string
    valueType?: string
    status?: string
    remark?: string
  }
  summary?: {
    acceptRecordCount?: number
    acceptedCount?: number
    rejectedCount?: number
    deadlineExpired?: boolean
    closestDistanceKm?: number
  }
  acceptRecords?: Array<{
    id?: number
    orderId?: number
    unitId?: number
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
    acceptTime?: string | Dayjs
    distanceKm?: number
    acceptResult?: string
    remark?: string
  }>
}

// 订单匹配记录 API
export const OrderMatchRecordApi = {
  // 查询订单匹配记录分页
  getOrderMatchRecordPage: async (params: any) => {
    return await request.get({ url: `/order/match-record/page`, params })
  },

  // 查询订单匹配记录详情
  getOrderMatchRecord: async (id: number) => {
    return await request.get<OrderMatchRecordDetail>({ url: `/order/match-record/get?id=` + id })
  },

  // 新增订单匹配记录
  createOrderMatchRecord: async (data: OrderMatchRecord) => {
    return await request.post({ url: `/order/match-record/create`, data })
  },

  // 修改订单匹配记录
  updateOrderMatchRecord: async (data: OrderMatchRecord) => {
    return await request.put({ url: `/order/match-record/update`, data })
  },

  // 删除订单匹配记录
  deleteOrderMatchRecord: async (id: number) => {
    return await request.delete({ url: `/order/match-record/delete?id=` + id })
  },

  /** 批量删除订单匹配记录 */
  deleteOrderMatchRecordList: async (ids: number[]) => {
    return await request.delete({ url: `/order/match-record/delete-list?ids=${ids.join(',')}` })
  },

  // 导出订单匹配记录 Excel
  exportOrderMatchRecord: async (params) => {
    return await request.download({ url: `/order/match-record/export-excel`, params })
  }
}
