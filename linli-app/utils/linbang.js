export const PRICING_MODE_OPTIONS = [
  { label: '一口价', value: 'FIXED_PRICE' },
  { label: '计时', value: 'HOURLY' },
  { label: '承包', value: 'CONTRACT' },
  { label: '按单位', value: 'BY_UNIT' },
  { label: '外包', value: 'OUTSOURCING' }
]

export const PRICE_ITEM_TYPE_OPTIONS = [
  { label: '人工费', value: 'LABOR' },
  { label: '材料费', value: 'MATERIAL' },
  { label: '附加费', value: 'EXTRA' },
  { label: '上门费', value: 'VISIT' },
  { label: '其他', value: 'CUSTOM' }
]

export const ORDER_BUSINESS_TABS = [
  { label: '全部', value: '' },
  { label: '待付款', value: 'WAIT_PAY' },
  { label: '待接单', value: 'WAIT_ACCEPT' },
  { label: '服务中', value: 'IN_SERVICE' },
  { label: '待评价', value: 'WAIT_REVIEW' },
  { label: '售后', value: 'AFTER_SALE' },
  { label: '已完成', value: 'FINISHED' },
  { label: '已退款', value: 'REFUNDED' }
]

export const REFUND_REASON_OPTIONS = [
  '不想购买了',
  '重复下单',
  '服务时间不合适',
  '服务未按约履行',
  '其他原因'
]

export const COMPLAINT_TYPE_OPTIONS = [
  '服务不满意',
  '未按时上门',
  '额外收费',
  '态度恶劣',
  '其他'
]

export const APPEAL_TYPE_OPTIONS = [
  '订单争议',
  '处罚申诉',
  '退款结果申诉',
  '服务凭证补充',
  '其他'
]

export const CREDIT_LEVEL_OPTIONS = [
  { label: '预警', value: 'WARNING' },
  { label: '正常', value: 'NORMAL' },
  { label: '优秀', value: 'EXCELLENT' },
  { label: '禁用', value: 'DISABLED' }
]

export const ROLE_APPLY_STATUS_OPTIONS = [
  { label: '可申请', value: 'AVAILABLE' },
  { label: '审核中', value: 'PENDING' },
  { label: '已开通', value: 'ENABLED' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' }
]

const PRICING_MODE_LABELS = PRICING_MODE_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const ORDER_SPLIT_MODE_LABELS = {
  DIRECT: '直接单',
  BY_PROGRESS: '按工序拆分',
  BY_PROCESS: '按流程拆分',
  BY_CONTENT: '按内容拆分',
  BY_PERSON: '按人数拆分'
}

const PRICE_ITEM_TYPE_LABELS = PRICE_ITEM_TYPE_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const ORDER_STATUS_LABELS = {
  PENDING_CREATE: '待生成',
  PENDING_PAY: '待支付',
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  SERVING: '服务中',
  PENDING_CONFIRM: '待确认',
  FINISHED: '已完成',
  AFTER_SALE: '售后中',
  REFUNDED: '已退款',
  CLOSED: '已关闭',
  APPEALING: '申诉中'
}

const ORDER_UNIT_STATUS_LABELS = {
  PENDING_CREATE: '待生成',
  PENDING_PAY: '待支付',
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  SERVING: '服务中',
  PENDING_CONFIRM: '待验收',
  FINISHED: '已完成',
  AFTER_SALE: '售后中',
  REFUNDED: '已退款',
  CLOSED: '已关闭',
  APPEALING: '申诉中'
}

const BUSINESS_CATEGORY_LABELS = {
  WAIT_ACCEPT: '待接单',
  IN_SERVICE: '服务中',
  FINISHED: '已完成',
  AFTER_SALE: '售后',
  WAIT_REVIEW: '待评价',
  WAIT_PAY: '待付款',
  REFUNDED: '已退款'
}

const DISPATCH_STATUS_LABELS = {
  WAITING: '待派单',
  PUSHING: '派单中',
  MATCHING: '匹配中',
  PUSHED: '已推送',
  FLOWED: '已流单',
  ACCEPTED: '已接单',
  EXPIRED: '已过期',
  FINISHED: '已结束',
  CLOSED: '已关闭'
}

const VERIFY_STATUS_LABELS = {
  PENDING: '待核销',
  VERIFIED: '已核销'
}

const PAY_STATUS_LABELS = {
  WAITING: '待支付',
  SUCCESS: '支付成功',
  FAILED: '支付失败',
  CLOSED: '已关闭',
  REFUND: '已退款',
  0: '待支付',
  10: '待支付',
  20: '支付成功',
  30: '已退款',
  40: '已关闭'
}

const TIMELINE_TYPE_LABELS = {
  ORDER: '订单',
  UNIT: '单元',
  PAY: '支付',
  REFUND: '退款',
  COMPLAINT: '投诉',
  APPEAL: '申诉',
  VERIFY: '核销',
  LOG: '日志',
  PREVIEW: '预览'
}

const OPERATE_TYPE_LABELS = {
  CREATE_ORDER: '创建订单',
  CANCEL_ORDER: '取消订单',
  START_UNIT_SERVICE: '开始服务',
  CONFIRM_UNIT_FINISH: '确认完工',
  UNLOCK_NEXT_UNIT: '解锁下一单元',
  ACCEPT_ORDER: '服务商接单',
  UPLOAD_DELIVERY_PROOF: '上传交付凭证',
  CREATE_PAY_ORDER: '创建支付单',
  PAY_SUCCESS: '支付成功',
  REFUND_APPLY: '提交退款申请',
  REFUND_SUCCESS: '退款成功',
  REFUND_FAILED: '退款失败',
  AUTO_FLOW_REFUND: '系统自动退款',
  ADMIN_UNLOCK_UNIT: '管理员解锁单元',
  AUDIT_APPEAL: '审核申诉'
}

const OPERATE_ROLE_LABELS = {
  USER: '用户',
  MERCHANT: '服务商',
  SYSTEM: '系统',
  ADMIN: '管理员'
}

const COMPLAINT_STATUS_LABELS = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  FINISHED: '已完结',
  REJECTED: '已驳回'
}

const APPEAL_STATUS_LABELS = {
  PENDING: '待审核',
  PROCESSING: '处理中',
  APPROVED: '已通过',
  REJECTED: '已驳回',
  FINISHED: '已完结'
}

const REFUND_AUDIT_STATUS_LABELS = {
  PENDING: '待审核',
  APPROVED: '已通过',
  REJECTED: '已驳回',
  WAITING: '退款处理中',
  SUCCESS: '退款成功',
  FAILURE: '退款失败'
}

const CREDIT_LEVEL_LABELS = CREDIT_LEVEL_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const ROLE_APPLY_STATUS_LABELS = ROLE_APPLY_STATUS_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

export function getPricingModeLabel(value) {
  return PRICING_MODE_LABELS[value] || value || '--'
}

export function formatOrderSplitModeLabel(value) {
  return ORDER_SPLIT_MODE_LABELS[value] || value || '--'
}

export function getPriceItemTypeLabel(value) {
  return PRICE_ITEM_TYPE_LABELS[value] || value || '--'
}

export function getOrderStatusLabel(value) {
  return ORDER_STATUS_LABELS[value] || value || '--'
}

export function getOrderUnitStatusLabel(value) {
  return ORDER_UNIT_STATUS_LABELS[value] || value || '--'
}

export function getBusinessCategoryLabel(value) {
  return BUSINESS_CATEGORY_LABELS[value] || value || '--'
}

export function getDispatchStatusLabel(value) {
  return DISPATCH_STATUS_LABELS[value] || value || '--'
}

export function getVerifyStatusLabel(value) {
  return VERIFY_STATUS_LABELS[value] || value || '--'
}

export function getPayStatusLabel(value) {
  return PAY_STATUS_LABELS[value] || value || '--'
}

export function getTimelineTypeLabel(value) {
  return TIMELINE_TYPE_LABELS[value] || value || '--'
}

export function getOperateTypeLabel(value) {
  return OPERATE_TYPE_LABELS[value] || value || '--'
}

export function getOperateRoleLabel(value) {
  return OPERATE_ROLE_LABELS[value] || value || '--'
}

export function getComplaintStatusLabel(value) {
  return COMPLAINT_STATUS_LABELS[value] || value || '--'
}

export function getAppealStatusLabel(value) {
  return APPEAL_STATUS_LABELS[value] || value || '--'
}

export function getRefundAuditStatusLabel(value) {
  return REFUND_AUDIT_STATUS_LABELS[value] || value || '--'
}

export function getCreditLevelLabel(value) {
  return CREDIT_LEVEL_LABELS[value] || value || '--'
}

export function getRoleApplyStatusLabel(value) {
  return ROLE_APPLY_STATUS_LABELS[value] || value || '--'
}

export function getRoleApplyName(code) {
  const roleNames = {
    PROMOTER: '推广员',
    PARTNER: '区域合作商',
    PLATFORM_OPERATOR: '平台管理员（请使用管理后台）',
    MERCHANT: '服务商',
    USER: '普通用户'
  }
  return roleNames[code] || code || '--'
}

export function buildAddressText(address) {
  if (!address) {
    return ''
  }
  return [
    address.province,
    address.city,
    address.district,
    address.street,
    address.detailAddress
  ].filter(Boolean).join(' ')
}

export function extractUploadedFile(result, fallbackUrl = '') {
  if (typeof result === 'number') {
    return {
      fileId: result,
      url: fallbackUrl
    }
  }
  const payload = result || {}
  return {
    fileId: payload.fileId || payload.id || 0,
    url: payload.url || payload.fileUrl || fallbackUrl || '',
    name: payload.name || payload.originalName || ''
  }
}

export function uniqueById(list, key = 'id') {
  const seen = {}
  return (list || []).filter((item) => {
    const value = item && item[key]
    if (value === undefined || value === null || seen[value]) {
      return false
    }
    seen[value] = true
    return true
  })
}

export function toYuanFromFen(value) {
  if (value === undefined || value === null || value === '') {
    return 0
  }
  const numberValue = Number(value)
  if (Number.isNaN(numberValue)) {
    return 0
  }
  return numberValue / 100
}
