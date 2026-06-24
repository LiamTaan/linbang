import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

/** 订单主信息 */
export interface OrderInfo {
  id: number
  orderNo?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  merchantId: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  categoryId?: number
  categoryName?: string
  title?: string
  pricingMode?: string
  budgetAmount: number
  orderAmount: number
  serviceDurationDesc: string
  quantity: number
  requireDesc: string
  addressId: number
  province: string
  city: string
  district: string
  street: string
  detailAddress: string
  longitude: number
  latitude: number
  needInvoice?: boolean
  needSplit?: boolean
  splitStatus: string
  agreementConfirmed?: boolean
  payOrderId: number
  status?: string
  createTime?: string | Dayjs
}

export interface OrderInfoDetail extends OrderInfo {
  updateTime?: string | Dayjs
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
  payRecord?: {
    id?: number
    merchantOrderId?: string
    subject?: string
    price?: number
    status?: number
    channelCode?: string
    channelOrderNo?: string
    refundPrice?: number
    expireTime?: string | Dayjs
    successTime?: string | Dayjs
    createTime?: string | Dayjs
}
  priceItems?: Array<{
    itemType?: string
    itemName?: string
    itemAmount?: number
    sortNo?: number
  }>
  attachments?: Array<{
    fileId?: number
    fileType?: string
    fileUrl?: string
    sortNo?: number
  }>
  units?: Array<{
    id?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    splitMode?: string
    prevUnitId?: number
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    status?: string
    acceptDeadlineTime?: string | Dayjs
    finishTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  acceptRecords?: Array<{
    id?: number
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
  proofs?: Array<{
    id?: number
    unitId?: number
    merchantId?: number
    fileId?: number
    proofType?: string
    proofDesc?: string
    proofTime?: string | Dayjs
    longitude?: number
    latitude?: number
  }>
  refunds?: Array<{
    id?: number
    payOrderId?: number
    merchantRefundId?: string
    status?: number
    statusName?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    refundPrice?: number
    reason?: string
    channelErrorMsg?: string
    successTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  complaints?: Array<{
    id?: number
    complaintNo?: string
    unitId?: number
    complainantUserId?: number
    respondentUserId?: number
    complaintType?: string
    content?: string
    status?: string
    resultDesc?: string
    handleTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  appeals?: Array<{
    id?: number
    appealNo?: string
    unitId?: number
    userId?: number
    appealType?: string
    content?: string
    status?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    auditTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  operateLogs?: Array<{
    id?: number
    unitId?: number
    operateType?: string
    operateRole?: string
    operateBy?: number
    beforeStatus?: string
    afterStatus?: string
    remark?: string
    operateTime?: string | Dayjs
  }>
  }

// 订单主 API
export const OrderInfoApi = {
  // 查询订单主分页
  getOrderInfoPage: async (params: any) => {
    return await request.get({ url: `/order/info/page`, params })
  },

  // 查询订单主详情
  getOrderInfo: async (id: number) => {
    return await request.get<OrderInfoDetail>({ url: `/order/info/get?id=` + id })
  },

  // 新增订单主
  createOrderInfo: async (data: OrderInfo) => {
    return await request.post({ url: `/order/info/create`, data })
  },

  // 修改订单主
  updateOrderInfo: async (data: OrderInfo) => {
    return await request.put({ url: `/order/info/update`, data })
  },

  // 删除订单主
  deleteOrderInfo: async (id: number) => {
    return await request.delete({ url: `/order/info/delete?id=` + id })
  },

  /** 批量删除订单主 */
  deleteOrderInfoList: async (ids: number[]) => {
    return await request.delete({ url: `/order/info/delete-list?ids=${ids.join(',')}` })
  },

  // 导出订单主 Excel
  exportOrderInfo: async (params) => {
    return await request.download({ url: `/order/info/export-excel`, params })
  }
}
