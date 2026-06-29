export const PRICING_MODE_OPTIONS = [
  { label: '一口价', value: 'FIXED_PRICE' },
  { label: '计时', value: 'HOURLY' },
  { label: '承包', value: 'CONTRACT' },
  { label: '按单位', value: 'BY_UNIT' },
  { label: '外包', value: 'OUTSOURCING' }
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

const PRICING_MODE_LABELS = PRICING_MODE_OPTIONS.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const ORDER_STATUS_LABELS = {
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
  PUSHING: '派单中',
  MATCHING: '匹配中',
  ACCEPTED: '已接单',
  EXPIRED: '已过期',
  FINISHED: '已结束'
}

const VERIFY_STATUS_LABELS = {
  PENDING: '待核销',
  VERIFIED: '已核销'
}

export function getPricingModeLabel(value) {
  return PRICING_MODE_LABELS[value] || value || '--'
}

export function getOrderStatusLabel(value) {
  return ORDER_STATUS_LABELS[value] || value || '--'
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
