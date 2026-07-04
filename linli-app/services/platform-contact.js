import { loadPlatformSettings } from '@/services/app-bootstrap'
import { getPlatformSettings } from '@/utils/auth'

const FEEDBACK_PAGE_URL = '/pages/feedback/feedback'

function normalizeValue(value) {
  return String(value || '').trim()
}

async function loadContactSettings(force = true) {
  const settings = await loadPlatformSettings(force).catch(() => getPlatformSettings() || {})
  return settings || {}
}

export function getServiceWechat(settings) {
  return normalizeValue(settings && settings.serviceWechat)
}

export function getServiceHotline(settings) {
  return normalizeValue(settings && settings.serviceHotline)
}

export function copyServiceWechat(settings, emptyTitle = '在线客服暂未配置') {
  const serviceWechat = getServiceWechat(settings)
  if (!serviceWechat) {
    uni.showToast({
      title: emptyTitle,
      icon: 'none'
    })
    return false
  }
  uni.setClipboardData({
    data: serviceWechat,
    success: () => {
      uni.showToast({
        title: '客服微信已复制',
        icon: 'success'
      })
    }
  })
  return true
}

export function callServiceHotline(settings, emptyTitle = '客服电话暂未配置') {
  const serviceHotline = getServiceHotline(settings)
  if (!serviceHotline) {
    uni.showToast({
      title: emptyTitle,
      icon: 'none'
    })
    return false
  }
  uni.makePhoneCall({
    phoneNumber: serviceHotline
  })
  return true
}

export function openHelpFeedback(currentPage = '') {
  if (currentPage === FEEDBACK_PAGE_URL) {
    return false
  }
  uni.navigateTo({
    url: FEEDBACK_PAGE_URL
  })
  return true
}

export async function openPlatformContact(options = {}) {
  const settings = await loadContactSettings(true)
  const itemList = []
  const actions = []
  const includeHelpEntry = options.includeHelpEntry !== false
  const currentPage = options.currentPage || ''

  if (getServiceWechat(settings)) {
    itemList.push('在线客服')
    actions.push(() => copyServiceWechat(settings))
  }
  if (getServiceHotline(settings)) {
    itemList.push('电话客服')
    actions.push(() => callServiceHotline(settings))
  }
  if (includeHelpEntry && currentPage !== FEEDBACK_PAGE_URL) {
    itemList.push('帮助与反馈')
    actions.push(() => openHelpFeedback(currentPage))
  }

  if (!itemList.length) {
    uni.showToast({
      title: options.emptyTitle || '客服暂未配置',
      icon: 'none'
    })
    return false
  }
  if (itemList.length === 1) {
    actions[0]()
    return true
  }
  uni.showActionSheet({
    itemList,
    success: ({ tapIndex }) => {
      const action = actions[tapIndex]
      if (action) {
        action()
      }
    }
  })
  return true
}
