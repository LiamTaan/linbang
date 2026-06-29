import request from '@/config/axios'
import type { Dayjs } from 'dayjs'

/** 拆分单元信息 */
export interface OrderUnit {
  id: number
  orderId?: number
  orderNo?: string
  unitNo?: string
  unitSeq?: number
  unitTitle: string
  unitContent?: string
  unitProgress?: string
  workerCount?: number
  maxAmountLimit?: number
  unitAmount?: number
  splitMode: string
  prevUnitId: number
  prevUnitNo?: string
  isLocked?: boolean
  lockReason: string
  merchantId: number
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  status?: string
  acceptDeadlineTime?: string | Dayjs
  finishTime?: string | Dayjs
  appealExpireTime?: string | Dayjs
  verifyStatus?: string
  verifyCode?: string
  verifyTime?: string | Dayjs
  verifyBy?: number
  verifyRemark?: string
  createTime?: string | Dayjs
}

export interface OrderUnitDetail extends OrderUnit {
  orderTitle?: string
  orderStatus?: string
  userId?: number
  userNo?: string
  userNickname?: string
  userMobile?: string
  merchantName?: string
  merchantContactName?: string
  merchantContactMobile?: string
  proofs?: Array<{
    id?: number
    fileId?: number
    fileUrl?: string
    fileHash?: string
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
    proofType?: string
    proofDesc?: string
    proofTime?: string | Dayjs
    deviceTime?: string | Dayjs
    addressText?: string
    longitude?: number
    latitude?: number
  }>
  acceptRecords?: Array<{
    id?: number
    merchantId?: number
    merchantName?: string
    merchantContactName?: string
    merchantContactMobile?: string
    acceptTime?: string | Dayjs
    distanceKm?: number
    acceptResult?: string
    remark?: string
  }>
  complaints?: Array<{
    id?: number
    complaintNo?: string
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
    userId?: number
    appealType?: string
    content?: string
    status?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    auditTime?: string | Dayjs
    appealExpireTime?: string | Dayjs
    createTime?: string | Dayjs
  }>
  operateLogs?: Array<{
    id?: number
    operateType?: string
    operateRole?: string
    operateBy?: number
    beforeStatus?: string
    afterStatus?: string
    remark?: string
    operateTime?: string | Dayjs
  }>
  timeline?: Array<{
    timelineType?: string
    bizId?: number
    title?: string
    content?: string
    status?: string
    eventTime?: string | Dayjs
  }>
}

// 拆分单元 API
export const OrderUnitApi = {
  // 查询拆分单元分页
  getOrderUnitPage: async (params: any) => {
    return await request.get({ url: `/order/unit/page`, params })
  },

  // 查询拆分单元详情
  getOrderUnit: async (id: number) => {
    return await request.get<OrderUnitDetail>({ url: `/order/unit/get?id=` + id })
  },

  // 新增拆分单元
  createOrderUnit: async (data: OrderUnit) => {
    return await request.post({ url: `/order/unit/create`, data })
  },

  // 修改拆分单元
  updateOrderUnit: async (data: OrderUnit) => {
    return await request.put({ url: `/order/unit/update`, data })
  },

  // 删除拆分单元
  deleteOrderUnit: async (id: number) => {
    return await request.delete({ url: `/order/unit/delete?id=` + id })
  },

  /** 批量删除拆分单元 */
  deleteOrderUnitList: async (ids: number[]) => {
    return await request.delete({ url: `/order/unit/delete-list?ids=${ids.join(',')}` })
  },

  // 导出拆分单元 Excel
  exportOrderUnit: async (params) => {
    return await request.download({ url: `/order/unit/export-excel`, params })
  }
}
