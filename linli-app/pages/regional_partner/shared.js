import { getRoleContext } from '@/api/member'
import { ensureRoleAccess } from '@/services/role-guard'

export async function ensurePartnerPageAccess(page, actionLabel) {
  const roleContext = await getRoleContext({ silent: true }).catch(() => ({}))
  page.roleContext = roleContext || {}
  return ensureRoleAccess({
    roleContext: page.roleContext,
    requiredRoleCode: 'PARTNER',
    actionLabel: actionLabel || '区域合作商页面',
    refreshContext: async () => {
      page.roleContext = await getRoleContext({ silent: true }).catch(() => ({}))
    }
  })
}

export function formatDateTime(value) {
  if (!value) {
    return '--'
  }
  if (typeof value === 'number' || /^\d{13}$/.test(`${value}`)) {
    const date = new Date(Number(value))
    if (Number.isNaN(date.getTime())) {
      return '--'
    }
    const year = date.getFullYear()
    const month = `${date.getMonth() + 1}`.padStart(2, '0')
    const day = `${date.getDate()}`.padStart(2, '0')
    const hour = `${date.getHours()}`.padStart(2, '0')
    const minute = `${date.getMinutes()}`.padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  }
  const normalized = `${value}`.replace('T', ' ')
  return normalized.slice(0, 16)
}

export function formatMoney(ctx, value) {
  return `¥${ctx.$fmt.formatMoney(value || 0)}`
}

export function getRegionDisplayName(item) {
  if (!item) {
    return '--'
  }
  return [item.province, item.city, item.district, item.streetName].filter(Boolean).join('')
}

export function getRegionStatusLabel(status) {
  const map = {
    ENABLE: '启用',
    DISABLE: '停用'
  }
  return map[status] || status || '--'
}

export function getEntryStatusLabel(status) {
  const map = {
    PENDING: '待审核',
    FIRST_APPROVED: '已通过',
    APPROVED: '已通过',
    REJECTED: '已驳回'
  }
  return map[status] || status || '--'
}

export function getDisputeTypeLabel(type) {
  const map = {
    COMPLAINT: '投诉',
    APPEAL: '申诉'
  }
  return map[type] || type || '--'
}

export function getDisputeStatusLabel(status) {
  const map = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    ESCALATED: '已升级',
    APPROVED: '已升级',
    REJECTED: '已处理'
  }
  return map[status] || status || '--'
}

export function getCoordinationStatusLabel(status) {
  const map = {
    PROCESSING: '处理中',
    ESCALATED: '已升级',
    FINISHED: '已完成'
  }
  return map[status] || status || '--'
}

export function getPriceReportStatusLabel(status) {
  const map = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    WITHDRAWN: '已撤回'
  }
  return map[status] || status || '--'
}

export function getInstructionCategoryLabel(category) {
  const map = {
    MEETING_NOTICE: '会议通知',
    SUPERIOR_INSTRUCTION: '上级指令'
  }
  return map[category] || category || '--'
}

export function getReadStatusLabel(status) {
  return status === 'READ' ? '已读' : '未读'
}

export function goBack() {
  uni.navigateBack({
    fail: () => {
      uni.switchTab({
        url: '/pages/my/my'
      })
    }
  })
}

export function navigateTo(url) {
  uni.navigateTo({
    url,
    fail: () => {
      uni.showToast({
        title: '页面暂不可达',
        icon: 'none'
      })
    }
  })
}

export function confirmAction(options) {
  return new Promise((resolve) => {
    uni.showModal({
      ...options,
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    })
  })
}

export function promptText(options) {
  return new Promise((resolve) => {
    uni.showModal({
      editable: true,
      placeholderText: options.placeholderText || '',
      content: options.content || '',
      title: options.title || '请输入',
      success: (res) => resolve(res.confirm ? (res.content || '').trim() : ''),
      fail: () => resolve('')
    })
  })
}
