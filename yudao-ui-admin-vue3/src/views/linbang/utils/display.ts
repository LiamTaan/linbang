export const CHANNEL_TYPE_OPTIONS = [
  { label: '站内信', value: 'IN_APP' },
  { label: '站内消息', value: 'INTERNAL_MESSAGE' },
  { label: '短信', value: 'SMS' },
  { label: '微信订阅', value: 'WX_SUBSCRIBE' },
  { label: '支付宝消息', value: 'ALIPAY' }
]

export const FLOW_TYPE_OPTIONS = [
  { label: '收入', value: 'IN' },
  { label: '支出', value: 'OUT' }
]

export const TRIGGER_TYPE_OPTIONS = [
  { label: '认证', value: 'AUTH' },
  { label: '服务商', value: 'MERCHANT' },
  { label: '订单', value: 'ORDER' },
  { label: '售后', value: 'AFTER_SALE' }
]

export const COMPLAINT_TYPE_OPTIONS = [
  { label: '服务不满意', value: '服务不满意' },
  { label: '未按时上门', value: '未按时上门' },
  { label: '额外收费', value: '额外收费' },
  { label: '态度恶劣', value: '态度恶劣' },
  { label: '其他', value: '其他' }
]

export const ACCEPT_RESULT_OPTIONS = [
  { label: '成功', value: 'SUCCESS' },
  { label: '失败', value: 'FAILED' }
]

export const SPLIT_STATUS_OPTIONS = [
  { label: '未拆单', value: 'UNSPLIT' },
  { label: '已拆单', value: 'SPLIT' },
  { label: '部分拆单', value: 'PARTIAL_SPLIT' }
]

export const SPLIT_MODE_OPTIONS = [
  { label: '直接派单', value: 'DIRECT' },
  { label: '按金额拆分', value: 'BY_AMOUNT' }
]

export const SEND_STATUS_OPTIONS = [
  { label: '待发送', value: 'PENDING' },
  { label: '发送成功', value: 'SUCCESS' },
  { label: '发送失败', value: 'FAILED' }
]

export const HANDLE_STATUS_OPTIONS = [
  { label: '待处理', value: 'PENDING' },
  { label: '处理中', value: 'PROCESSING' },
  { label: '已完成', value: 'FINISHED' },
  { label: '已驳回', value: 'REJECTED' }
]

export const HELP_FEEDBACK_STATUS_OPTIONS = [
  { label: '待处理', value: 'PENDING' },
  { label: '处理中', value: 'PROCESSING' },
  { label: '已完成', value: 'FINISHED' }
]

export const RISK_EVENT_STATUS_OPTIONS = [
  { label: '待处理', value: 'PENDING' },
  { label: '已处理', value: 'HANDLED' },
  { label: '已忽略', value: 'IGNORED' }
]

export const PRICE_REPORT_STATUS_OPTIONS = [
  { label: '待处理', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' }
]

export const ENABLE_STATUS_OPTIONS = [
  { label: '启用', value: 'ENABLE' },
  { label: '禁用', value: 'DISABLE' }
]

export const ACCEPT_STATUS_OPTIONS = [
  { label: '开启接单', value: 'ENABLE' },
  { label: '暂停接单', value: 'DISABLE' }
]

export const BOOLEAN_YES_NO_OPTIONS = [
  { label: '是', value: true },
  { label: '否', value: false }
]

export const AUTO_REVIEW_OPTIONS = [
  { label: '自动评价', value: true },
  { label: '人工评价', value: false }
]

export const PLATFORM_CONFIG_CATEGORY_OPTIONS = [
  { label: '平台配置', value: 'linbang_platform' },
  { label: '协议文案', value: 'linbang_agreement' }
]

export const RISK_RULE_GROUP_OPTIONS = [
  { label: '订单', value: 'ORDER' },
  { label: '退款', value: 'REFUND' },
  { label: '提现', value: 'WITHDRAW' },
  { label: '匹配', value: 'MATCH' },
  { label: '售后', value: 'AFTER_SALE' }
]

export const RISK_RULE_VALUE_TYPE_OPTIONS = [
  { label: '整数', value: 'INTEGER' },
  { label: '分', value: 'FEN' },
  { label: '小数', value: 'DECIMAL' }
]

export const SENSITIVE_WORD_TYPE_OPTIONS = [
  { label: '违法违规', value: 'ILLEGAL' },
  { label: '风险信息', value: 'RISK' },
  { label: '联系方式', value: 'CONTACT' }
]

export const SENSITIVE_WORD_MATCH_TYPE_OPTIONS = [{ label: '包含匹配', value: 'CONTAINS' }]

export const COMMISSION_STATUS_OPTIONS = [
  { label: '待结算', value: 'PENDING' },
  { label: '已结算', value: 'SETTLED' },
  { label: '已失效', value: 'INVALID' }
]

export const MATCH_STATUS_OPTIONS = [
  { label: '已推送', value: 'PUSHED' },
  { label: '已接单', value: 'ACCEPTED' },
  { label: '已拒绝', value: 'REJECTED' }
]

const CHANNEL_TYPE_LABELS: Record<string, string> = {
  IN_APP: '站内信',
  INTERNAL_MESSAGE: '站内消息',
  SMS: '短信',
  WX_SUBSCRIBE: '微信订阅',
  ALIPAY: '支付宝消息'
}

const FLOW_TYPE_LABELS: Record<string, string> = {
  IN: '收入',
  OUT: '支出'
}

const TRIGGER_TYPE_LABELS: Record<string, string> = {
  AUTH: '认证',
  MERCHANT: '服务商',
  ORDER: '订单',
  AFTER_SALE: '售后'
}

const GENDER_LABELS: Record<string, string> = {
  0: '未知',
  1: '男',
  2: '女'
}

const COMPLAINT_TYPE_LABELS: Record<string, string> = {
  服务不满意: '服务不满意',
  未按时上门: '未按时上门',
  额外收费: '额外收费',
  态度恶劣: '态度恶劣',
  其他: '其他'
}

const APPEAL_TYPE_LABELS: Record<string, string> = {
  并非恶意取消: '并非恶意取消'
}

const PROOF_TYPE_LABELS: Record<string, string> = {}

const BLACK_TYPE_LABELS: Record<string, string> = {}

const RISK_LEVEL_LABELS: Record<string, string> = {
  LOW: '低',
  MEDIUM: '中',
  HIGH: '高'
}

const ACCEPT_RESULT_LABELS: Record<string, string> = {
  SUCCESS: '成功',
  FAILED: '失败'
}

const SPLIT_STATUS_LABELS: Record<string, string> = {
  UNSPLIT: '未拆单',
  SPLIT: '已拆单',
  PARTIAL_SPLIT: '部分拆单'
}

const SPLIT_MODE_LABELS: Record<string, string> = {
  DIRECT: '直接派单',
  BY_AMOUNT: '按金额拆分'
}

const SEND_STATUS_LABELS: Record<string, string> = {
  PENDING: '待发送',
  SUCCESS: '发送成功',
  FAILED: '发送失败'
}

const VERIFY_RESULT_LABELS: Record<string, string> = {
  PASS: '通过',
  FAIL: '不通过',
  PENDING: '待核验'
}

const ENABLE_STATUS_LABELS: Record<string, string> = {
  ENABLE: '启用',
  DISABLE: '禁用'
}

const ACCEPT_STATUS_LABELS: Record<string, string> = {
  ENABLE: '开启接单',
  DISABLE: '暂停接单'
}

const TEMPLATE_TYPE_LABELS: Record<string, string> = {
  ORDER: '订单',
  WALLET: '钱包'
}

const RISK_RULE_GROUP_LABELS: Record<string, string> = {
  ORDER: '订单',
  REFUND: '退款',
  WITHDRAW: '提现',
  MATCH: '匹配',
  AFTER_SALE: '售后'
}

const RISK_RULE_VALUE_TYPE_LABELS: Record<string, string> = {
  INTEGER: '整数',
  FEN: '分',
  DECIMAL: '小数'
}

const SENSITIVE_WORD_TYPE_LABELS: Record<string, string> = {
  ILLEGAL: '违法违规',
  RISK: '风险信息',
  CONTACT: '联系方式'
}

const SENSITIVE_WORD_MATCH_TYPE_LABELS: Record<string, string> = {
  CONTAINS: '包含匹配'
}

const PLATFORM_CONFIG_CATEGORY_LABELS: Record<string, string> = {
  linbang_platform: '平台配置',
  linbang_agreement: '协议文案'
}

const BIZ_TYPE_LABELS: Record<string, string> = {
  ORDER: '订单',
  ORDER_INFO: '订单',
  ORDER_UNIT: '订单单元',
  UNIT: '订单单元',
  ORDER_ABNORMAL: '异常订单',
  ABNORMAL: '异常订单',
  COMPLAINT: '投诉',
  APPEAL: '申诉',
  WITHDRAW: '提现',
  WITHDRAW_APPLY: '提现申请',
  REFUND_APPLY: '退款申请',
  REFUND_SUCCESS: '退款成功',
  REFUND_FAILED: '退款失败',
  ROLE_APPLY: '身份申请',
  MERCHANT_ENTRY: '服务商入驻',
  PRICE_REPORT: '价格申报',
  QUALIFICATION_EXPIRY: '资质到期',
  QUALIFICATION_EXPIRE_REMINDER_D7: '资质到期提醒 D-7',
  QUALIFICATION_EXPIRE_REMINDER_D1: '资质到期提醒 D-1',
  QUALIFICATION_EXPIRE_DISABLE: '资质到期限制接单'
}

const HANDLE_STATUS_LABELS: Record<string, string> = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  FINISHED: '已完成',
  REJECTED: '已驳回'
}

const HELP_FEEDBACK_STATUS_LABELS: Record<string, string> = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  FINISHED: '已完成'
}

const RISK_EVENT_STATUS_LABELS: Record<string, string> = {
  PENDING: '待处理',
  HANDLED: '已处理',
  IGNORED: '已忽略'
}

const COMPLAINT_STATUS_LABELS: Record<string, string> = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  FINISHED: '已完结',
  REJECTED: '已驳回'
}

const APPEAL_STATUS_LABELS: Record<string, string> = {
  PENDING: '待审核',
  PROCESSING: '处理中',
  APPROVED: '通过',
  REJECTED: '驳回',
  FINISHED: '已完结'
}

const COMMISSION_STATUS_LABELS: Record<string, string> = {
  PENDING: '待结算',
  SETTLED: '已结算',
  INVALID: '已失效'
}

const CONVERT_STATUS_LABELS: Record<string, string> = {
  CONVERTED: '已转化',
  PENDING: '待转化',
  INVALID: '已失效'
}

const MATCH_STATUS_LABELS: Record<string, string> = {
  PUSHED: '已推送',
  ACCEPTED: '已接单',
  REJECTED: '已拒绝'
}

const PRICE_REPORT_STATUS_LABELS: Record<string, string> = {
  PENDING: '待处理',
  APPROVED: '已通过',
  REJECTED: '已驳回'
}

const OPERATE_STATUS_LABELS: Record<string, string> = {
  PENDING_CREATE: '待生成',
  PENDING_PAY: '待支付',
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  SERVING: '服务中',
  PENDING_CONFIRM: '待确认/待验收',
  AFTER_SALE: '售后中',
  APPEALING: '申诉中',
  FINISHED: '已完成',
  REFUNDED: '已退款',
  CLOSED: '已关闭'
}

const OPERATE_TYPE_LABELS: Record<string, string> = {
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
  REFUND_FAILED: '退款失败'
}

const OPERATE_ROLE_LABELS: Record<string, string> = {
  USER: '用户',
  MERCHANT: '服务商',
  SYSTEM: '系统',
  ADMIN: '管理员'
}

const ORDER_STATUS_LABELS: Record<string, string> = {
  PENDING_PAY: '待支付',
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  SERVING: '服务中',
  PENDING_CONFIRM: '待确认',
  AFTER_SALE: '售后中',
  FINISHED: '已完成',
  REFUNDED: '已退款',
  CLOSED: '已关闭'
}

const ORDER_UNIT_STATUS_LABELS: Record<string, string> = {
  PENDING_CREATE: '待生成',
  PENDING_ACCEPT: '待接单',
  ACCEPTED: '已接单',
  SERVING: '服务中',
  PENDING_CONFIRM: '待验收',
  APPEALING: '申诉中',
  FINISHED: '已完成',
  REFUNDED: '已退款',
  CLOSED: '已关闭'
}

const formatValueByMap = (value: string | number | undefined, labelMap: Record<string, string>) => {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return labelMap[value] || value
}

export const formatChannelType = (value?: string) => formatValueByMap(value, CHANNEL_TYPE_LABELS)

export const formatFlowType = (value?: string) => formatValueByMap(value, FLOW_TYPE_LABELS)

export const formatTriggerType = (value?: string) => formatValueByMap(value, TRIGGER_TYPE_LABELS)

export const formatGender = (value?: number) => formatValueByMap(value, GENDER_LABELS)

export const formatComplaintType = (value?: string) => formatValueByMap(value, COMPLAINT_TYPE_LABELS)

export const formatAppealType = (value?: string) => formatValueByMap(value, APPEAL_TYPE_LABELS)

export const formatProofType = (value?: string) => formatValueByMap(value, PROOF_TYPE_LABELS)

export const formatBlackType = (value?: string) => formatValueByMap(value, BLACK_TYPE_LABELS)

export const formatRiskLevel = (value?: string) => formatValueByMap(value, RISK_LEVEL_LABELS)

export const formatAcceptResult = (value?: string) => formatValueByMap(value, ACCEPT_RESULT_LABELS)

export const formatSplitStatus = (value?: string) => formatValueByMap(value, SPLIT_STATUS_LABELS)

export const formatSplitMode = (value?: string) => formatValueByMap(value, SPLIT_MODE_LABELS)

export const formatSendStatus = (value?: string) => formatValueByMap(value, SEND_STATUS_LABELS)

export const formatVerifyResult = (value?: string) => formatValueByMap(value, VERIFY_RESULT_LABELS)

export const formatEnableStatus = (value?: string) => formatValueByMap(value, ENABLE_STATUS_LABELS)

export const formatAcceptStatus = (value?: string) => formatValueByMap(value, ACCEPT_STATUS_LABELS)

export const formatTemplateType = (value?: string) => formatValueByMap(value, TEMPLATE_TYPE_LABELS)

export const formatRiskRuleGroup = (value?: string) => formatValueByMap(value, RISK_RULE_GROUP_LABELS)

export const formatRiskRuleValueType = (value?: string) =>
  formatValueByMap(value, RISK_RULE_VALUE_TYPE_LABELS)

export const formatSensitiveWordType = (value?: string) =>
  formatValueByMap(value, SENSITIVE_WORD_TYPE_LABELS)

export const formatSensitiveWordMatchType = (value?: string) =>
  formatValueByMap(value, SENSITIVE_WORD_MATCH_TYPE_LABELS)

export const formatPlatformConfigCategory = (value?: string) =>
  formatValueByMap(value, PLATFORM_CONFIG_CATEGORY_LABELS)

export const formatBizType = (value?: string) => formatValueByMap(value, BIZ_TYPE_LABELS)

export const formatHandleStatus = (value?: string) => formatValueByMap(value, HANDLE_STATUS_LABELS)

export const formatHelpFeedbackStatus = (value?: string) =>
  formatValueByMap(value, HELP_FEEDBACK_STATUS_LABELS)

export const formatRiskEventStatus = (value?: string) => formatValueByMap(value, RISK_EVENT_STATUS_LABELS)

export const formatComplaintStatus = (value?: string) => formatValueByMap(value, COMPLAINT_STATUS_LABELS)

export const formatAppealStatus = (value?: string) => formatValueByMap(value, APPEAL_STATUS_LABELS)

export const formatCommissionStatus = (value?: string) =>
  formatValueByMap(value, COMMISSION_STATUS_LABELS)

export const formatConvertStatus = (value?: string) => formatValueByMap(value, CONVERT_STATUS_LABELS)

export const formatMatchStatus = (value?: string) => formatValueByMap(value, MATCH_STATUS_LABELS)

export const formatPriceReportStatus = (value?: string) =>
  formatValueByMap(value, PRICE_REPORT_STATUS_LABELS)

export const getSendStatusTagType = (value?: string) => {
  if (value === 'SUCCESS') {
    return 'success'
  }
  if (value === 'FAILED') {
    return 'danger'
  }
  return 'warning'
}

export const getPriceReportStatusTagType = (value?: string) => {
  if (value === 'APPROVED') {
    return 'success'
  }
  if (value === 'REJECTED') {
    return 'danger'
  }
  return 'warning'
}

export const getCommissionStatusTagType = (value?: string) => {
  if (value === 'SETTLED') {
    return 'success'
  }
  if (value === 'INVALID') {
    return 'info'
  }
  return 'warning'
}

export const formatBooleanYesNo = (value?: boolean) => {
  if (value === true) {
    return '是'
  }
  if (value === false) {
    return '否'
  }
  return '-'
}

export const formatVisibleStatus = (value?: boolean) => {
  if (value === true) {
    return '可见'
  }
  if (value === false) {
    return '不可见'
  }
  return '-'
}

export const formatAutoReviewLabel = (value?: boolean) => {
  if (value === true) {
    return '自动评价'
  }
  if (value === false) {
    return '人工评价'
  }
  return '-'
}

export const formatOperateStatus = (value?: string) => formatValueByMap(value, OPERATE_STATUS_LABELS)

export const formatOperateType = (value?: string) => formatValueByMap(value, OPERATE_TYPE_LABELS)

export const formatOperateRole = (value?: string) => formatValueByMap(value, OPERATE_ROLE_LABELS)

export const formatOrderStatus = (value?: string) => formatValueByMap(value, ORDER_STATUS_LABELS)

export const formatOrderUnitStatus = (value?: string) =>
  formatValueByMap(value, ORDER_UNIT_STATUS_LABELS)
