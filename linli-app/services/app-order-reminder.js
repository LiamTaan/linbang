import { getMessageRecordPage, getMessageSetting, submitVoicePlayedFeedback } from '@/api/message'
import { getMerchantAcceptStatus, getMerchantDispatchSetting } from '@/api/merchant'
import { syncMessageUnreadCount } from '@/services/message-unread'
import { hasLogin } from '@/utils/auth'
import { normalizeAppRoute, openAppRoute } from '@/utils/navigation'

const REMINDER_POLL_INTERVAL_MS = 15000
const REMINDER_CONTEXT_TTL_MS = 60000
const LAST_UNREAD_ORDER_MESSAGE_ID_KEY = 'linbang_last_unread_order_message_id'
const LAST_NOTIFIED_ORDER_MESSAGE_ID_KEY = 'linbang_last_notified_order_message_id'
const REMINDER_SETTING_CHANGED_EVENT = 'linbang:reminder-setting-updated'
const SESSION_UPDATED_EVENT = 'linbang:session-updated'
const SESSION_CLEARED_EVENT = 'linbang:session-cleared'

const state = {
  timer: null,
  pushListenerBound: false,
  eventListenersBound: false,
  checking: false,
  baselineReady: false,
  reminderContext: null,
  reminderContextExpireAt: 0,
  lastUnreadOrderMessageId: Number(uni.getStorageSync(LAST_UNREAD_ORDER_MESSAGE_ID_KEY) || 0),
  lastNotifiedOrderMessageId: Number(uni.getStorageSync(LAST_NOTIFIED_ORDER_MESSAGE_ID_KEY) || 0)
}

function isAppRuntimeReady() {
  return typeof plus !== 'undefined'
}

function setCachedUnreadOrderMessageId(id) {
  const value = Number(id || 0)
  state.lastUnreadOrderMessageId = Number.isFinite(value) ? value : 0
  uni.setStorageSync(LAST_UNREAD_ORDER_MESSAGE_ID_KEY, state.lastUnreadOrderMessageId)
}

function setCachedNotifiedOrderMessageId(id) {
  const value = Number(id || 0)
  state.lastNotifiedOrderMessageId = Number.isFinite(value) ? value : 0
  uni.setStorageSync(LAST_NOTIFIED_ORDER_MESSAGE_ID_KEY, state.lastNotifiedOrderMessageId)
}

function resetReminderState() {
  state.baselineReady = false
  state.reminderContext = null
  state.reminderContextExpireAt = 0
  setCachedUnreadOrderMessageId(0)
  setCachedNotifiedOrderMessageId(0)
}

function clearReminderTimer() {
  if (!state.timer) {
    return
  }
  clearInterval(state.timer)
  state.timer = null
}

function buildReminderTitle(record) {
  return record.title || '邻里互助订单提醒'
}

function buildReminderContent(record) {
  return record.contentSnapshot || '您有新的订单消息，请及时查看。'
}

function resolveReminderRoute(payload) {
  const routeValue = payload && payload.routeValue ? String(payload.routeValue).trim() : ''
  const safeRoute = normalizeAppRoute(routeValue)
  if (safeRoute) {
    return safeRoute
  }
  if (payload && payload.messageCategory === 'ORDER' && payload.bizId) {
    return `/pages/split_order_details/split_order_details?orderId=${payload.bizId}`
  }
  uni.setStorageSync('linbang_news_category', 'ORDER')
  return '/pages/news/news'
}

function openReminderRoute(payload) {
  const route = resolveReminderRoute(payload)
  if (!route) {
    return
  }
  openAppRoute(route)
}

function parseReminderPayload(rawPayload) {
  if (!rawPayload) {
    return null
  }
  if (typeof rawPayload === 'object') {
    return rawPayload
  }
  try {
    return JSON.parse(rawPayload)
  } catch (error) {
    return null
  }
}

function bindPushClickListener() {
  if (state.pushListenerBound || !isAppRuntimeReady() || !plus.push || !plus.push.addEventListener) {
    return
  }
  plus.push.addEventListener('click', (message) => {
    const payload = parseReminderPayload(message && message.payload)
    if (!payload || payload.source !== 'linbang-order-reminder') {
      return
    }
    openReminderRoute(payload)
  }, false)
  state.pushListenerBound = true
}

function requestNotificationPermission() {
  if (!isAppRuntimeReady() || !plus.android || !plus.android.requestPermissions) {
    return
  }
  try {
    plus.android.requestPermissions(['android.permission.POST_NOTIFICATIONS'], () => {}, () => {})
  } catch (error) {
  }
}

async function loadReminderContext(force = false) {
  const now = Date.now()
  if (!force && state.reminderContext && now < state.reminderContextExpireAt) {
    return state.reminderContext
  }
  const [messageSetting, dispatchSetting, acceptStatus] = await Promise.all([
    getMessageSetting().catch(() => null),
    getMerchantDispatchSetting().catch(() => null),
    getMerchantAcceptStatus().catch(() => null)
  ])
  const dispatchEnabled = !!(dispatchSetting && dispatchSetting.dispatchEnabled !== false)
  const acceptEnabled = !!(acceptStatus && acceptStatus.acceptStatus === 'ENABLE')
  const merchantVoiceEnabled = !!(dispatchSetting && dispatchSetting.voiceRemindEnabled !== false)
  const messageVoiceEnabled = !(messageSetting && messageSetting.voiceReadEnabled === false)
  const popupEnabled = !(messageSetting && messageSetting.popupEnabled === false)
  const shouldMonitor = dispatchEnabled && acceptEnabled && merchantVoiceEnabled
  state.reminderContext = {
    shouldMonitor,
    shouldSound: shouldMonitor && messageVoiceEnabled,
    shouldPopup: shouldMonitor && popupEnabled,
    shouldVibrate: shouldMonitor,
    shouldBadge: true
  }
  state.reminderContextExpireAt = now + REMINDER_CONTEXT_TTL_MS
  return state.reminderContext
}

async function getLatestUnreadOrderMessage() {
  const page = await getMessageRecordPage({
    pageNo: 1,
    pageSize: 1,
    sendStatus: 'SUCCESS',
    messageCategory: 'ORDER',
    readStatus: 'UNREAD'
  }, { silent: true }).catch(() => ({ list: [] }))
  return page && page.list && page.list.length ? page.list[0] : null
}

function updateAppBadge(count = 0) {
  if (!isAppRuntimeReady() || !plus.runtime || !plus.runtime.setBadgeNumber) {
    return
  }
  try {
    plus.runtime.setBadgeNumber(Number(count || 0))
  } catch (error) {
  }
}

async function triggerOrderReminder(record, reminderContext) {
  const payload = {
    source: 'linbang-order-reminder',
    recordId: record.id,
    bizId: record.bizId,
    routeValue: record.routeValue || '',
    messageCategory: record.messageCategory || 'ORDER'
  }
  if (isAppRuntimeReady() && plus.push && plus.push.createMessage && reminderContext.shouldPopup) {
    try {
      plus.push.createMessage(buildReminderContent(record), JSON.stringify(payload), {
        title: buildReminderTitle(record),
        cover: false
      })
    } catch (error) {
    }
  }
  if (reminderContext.shouldSound) {
    try {
      if (isAppRuntimeReady() && plus.device && plus.device.beep) {
        plus.device.beep(1)
      }
    } catch (error) {
    }
    try {
      await submitVoicePlayedFeedback({ recordId: record.id })
    } catch (error) {
    }
  }
  if (reminderContext.shouldVibrate) {
    try {
      uni.vibrateLong()
    } catch (error) {
    }
  }
  setCachedNotifiedOrderMessageId(record.id)
}

async function runReminderCheck(forceContext = false) {
  if (!isAppRuntimeReady() || !hasLogin() || state.checking) {
    return
  }
  state.checking = true
  try {
    const reminderContext = await loadReminderContext(forceContext)
    const latestRecord = await getLatestUnreadOrderMessage()
    const latestRecordId = Number((latestRecord && latestRecord.id) || 0)
    setCachedUnreadOrderMessageId(latestRecordId)
    const unreadCount = reminderContext.shouldBadge
      ? await syncMessageUnreadCount({ silent: true }).catch(() => 0)
      : 0
    updateAppBadge(unreadCount)
    if (!state.baselineReady) {
      state.baselineReady = true
      return
    }
    if (!latestRecordId || latestRecordId === state.lastNotifiedOrderMessageId) {
      return
    }
    if (!reminderContext.shouldMonitor) {
      return
    }
    await triggerOrderReminder(latestRecord, reminderContext)
  } finally {
    state.checking = false
  }
}

function bindReminderEvents() {
  if (state.eventListenersBound) {
    return
  }
  uni.$on(REMINDER_SETTING_CHANGED_EVENT, () => {
    state.reminderContextExpireAt = 0
    runReminderCheck(true)
  })
  uni.$on(SESSION_UPDATED_EVENT, () => {
    resetReminderState()
    startAppOrderReminder()
  })
  uni.$on(SESSION_CLEARED_EVENT, () => {
    clearReminderTimer()
    resetReminderState()
    updateAppBadge(0)
  })
  state.eventListenersBound = true
}

export function notifyReminderSettingChanged() {
  uni.$emit(REMINDER_SETTING_CHANGED_EVENT)
}

export function startAppOrderReminder() {
  if (!isAppRuntimeReady()) {
    return
  }
  bindPushClickListener()
  bindReminderEvents()
  requestNotificationPermission()
  if (!state.timer) {
    state.timer = setInterval(() => {
      runReminderCheck(false)
    }, REMINDER_POLL_INTERVAL_MS)
  }
  runReminderCheck(false)
}

export function refreshAppOrderReminder() {
  state.reminderContextExpireAt = 0
  return runReminderCheck(true)
}

export function stopAppOrderReminder() {
  clearReminderTimer()
  resetReminderState()
  updateAppBadge(0)
}
