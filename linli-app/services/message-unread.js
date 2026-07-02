import { getUnreadCount } from '@/api/message'
import { hasLogin } from '@/utils/auth'

const MESSAGE_UNREAD_CACHE_KEY = 'linbang_message_unread_count'
export const MESSAGE_UNREAD_CHANGED_EVENT = 'linbang:message-unread-changed'

export function normalizeMessageUnreadCount(count) {
  const value = Number(count || 0)
  return Number.isFinite(value) && value > 0 ? Math.floor(value) : 0
}

export function getCachedMessageUnreadCount() {
  return normalizeMessageUnreadCount(uni.getStorageSync(MESSAGE_UNREAD_CACHE_KEY))
}

export function setMessageUnreadCount(count) {
  const value = normalizeMessageUnreadCount(count)
  uni.setStorageSync(MESSAGE_UNREAD_CACHE_KEY, value)
  uni.$emit(MESSAGE_UNREAD_CHANGED_EVENT, value)
  return value
}

export async function syncMessageUnreadCount(options = {}) {
  if (!hasLogin()) {
    return setMessageUnreadCount(0)
  }
  try {
    const count = await getUnreadCount(options)
    return setMessageUnreadCount(count)
  } catch (error) {
    return getCachedMessageUnreadCount()
  }
}
