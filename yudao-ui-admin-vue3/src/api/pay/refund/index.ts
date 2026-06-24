import request from '@/config/axios'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface RefundVO {
  id: number
  merchantId: number
  appId: number
  channelId: number
  channelCode: string
  orderId: string
  tradeNo: string
  merchantOrderId: string
  merchantRefundNo: string
  notifyUrl: string
  notifyStatus: number
  status: number
  type: number
  payAmount: number
  refundAmount: number
  reason: string
  userIp: string
  channelOrderNo: string
  channelRefundNo: string
  channelErrorCode: string
  channelErrorMsg: string
  channelExtras: string
  expireTime: Date
  successTime: Date
  notifyTime: Date
  createTime: Date
}

export interface RefundDetailVO {
  id?: number
  no?: string
  merchantRefundId?: string
  channelRefundNo?: string
  merchantOrderId?: string
  channelOrderNo?: string
  appId?: number
  appName?: string
  needAudit?: boolean
  auditStatus?: string
  auditRemark?: string
  rejectReason?: string
  auditBy?: number
  auditTime?: Date
  payPrice?: number
  refundPrice?: number
  status?: number
  successTime?: Date
  createTime?: Date
  updateTime?: Date
  channelCode?: string
  reason?: string
  userIp?: string
  notifyUrl?: string
  channelErrorCode?: string
  channelErrorMsg?: string
  channelNotifyData?: string
  bizContext?: RefundBizContextVO
}

export interface RefundBizContextVO {
  payRefundId?: number
  merchantRefundId?: string
  orderId?: number
  unitId?: number
  order?: {
    id?: number
    orderNo?: string
    userId?: number
    merchantId?: number
    title?: string
    pricingMode?: string
    orderAmount?: number
    splitStatus?: string
    status?: string
    payOrderId?: number
    createTime?: Date | string
  }
  unit?: {
    id?: number
    orderId?: number
    unitNo?: string
    unitSeq?: number
    unitTitle?: string
    unitAmount?: number
    isLocked?: boolean
    lockReason?: string
    merchantId?: number
    status?: string
    createTime?: Date | string
  }
  walletFlows?: {
    id?: number
    flowNo?: string
    userId?: number
    walletAccountId?: number
    bizType?: string
    flowType?: string
    changeAmount?: number
    beforeAmount?: number
    afterAmount?: number
    relatedOrderId?: number
    relatedUnitId?: number
    relatedPayOrderId?: number
    relatedRefundId?: number
    remark?: string
    createTime?: Date | string
  }[]
  complaints?: {
    id?: number
    complaintNo?: string
    orderId?: number
    unitId?: number
    complainantUserId?: number
    respondentUserId?: number
    complaintType?: string
    content?: string
    status?: string
    resultDesc?: string
    handleTime?: Date | string
    createTime?: Date | string
  }[]
  appeals?: {
    id?: number
    appealNo?: string
    orderId?: number
    unitId?: number
    userId?: number
    appealType?: string
    content?: string
    status?: string
    auditStatus?: string
    auditRemark?: string
    rejectReason?: string
    auditTime?: Date | string
    createTime?: Date | string
  }[]
  operateLogs?: {
    id?: number
    orderId?: number
    unitId?: number
    operateType?: string
    operateRole?: string
    operateBy?: number
    beforeStatus?: string
    afterStatus?: string
    remark?: string
    operateTime?: Date | string
  }[]
}

export interface RefundAuditReqVO {
  id: number
  auditStatus: 'APPROVED' | 'REJECTED'
  auditRemark?: string
  rejectReason?: string
}

// 查询列表退款订单
export const getRefundPage = (params: any) => {
  return request.get<PageResult<RefundDetailVO[]>>({ url: '/pay/refund/page', params })
}

// 查询详情退款订单
export const getRefund = (id: number) => {
  return request.get<RefundDetailVO>({ url: '/pay/refund/get?id=' + id })
}

export const getRefundBizContext = (payRefundId: number) => {
  return request.get<RefundBizContextVO>({ url: '/admin-api/pay/refund-context/get?payRefundId=' + payRefundId })
}

// 审核退款订单
export const auditRefund = (data: RefundAuditReqVO, verifyToken?: string) => {
  return request.post({
    url: '/pay/refund/audit',
    data,
    headers: buildDynamicKeyHeaders(verifyToken)
  })
}

// 新增退款订单
export const createRefund = (data: RefundVO) => {
  return request.post({ url: '/pay/refund/create', data })
}

// 修改退款订单
export const updateRefund = (data: RefundVO) => {
  return request.put({ url: '/pay/refund/update', data })
}

// 删除退款订单
export const deleteRefund = (id: number) => {
  return request.delete({ url: '/pay/refund/delete?id=' + id })
}

// 导出退款订单
export const exportRefund = (params: any) => {
  return request.download({ url: '/pay/refund/export-excel', params })
}
