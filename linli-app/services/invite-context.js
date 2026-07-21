import { bindInviteCode } from '@/api/promote'
import { APP_CONFIG } from '@/config/app'
import { hasLogin } from '@/utils/auth'

const STORAGE_KEY = APP_CONFIG.storageKeys.pendingInvite

function normalizeInviteCode(value) {
  return `${value || ''}`.trim().toUpperCase()
}

function parseScene(scene) {
  let decoded = `${scene || ''}`
  try {
    decoded = decodeURIComponent(decoded)
  } catch (error) {}
  const params = {}
  decoded.split('&').forEach((part) => {
    const [key, value] = part.split('=')
    if (key) params[key] = value || ''
  })
  return normalizeInviteCode(params.p || params.inviteCode || params.code)
}

export function captureInviteContext(options = {}) {
  const query = options.query || options || {}
  const directCode = normalizeInviteCode(query.inviteCode || query.code)
  const sceneCode = parseScene(query.scene)
  const inviteCode = directCode || sceneCode
  if (!inviteCode) return null
  const context = {
    inviteCode,
    sourceChannel: query.sourceChannel || (sceneCode ? 'QRCODE' : 'SHARE_CARD'),
    sourcePage: options.path || query.sourcePage || 'pages/index/index',
    capturedAt: Date.now()
  }
  uni.setStorageSync(STORAGE_KEY, context)
  return context
}

export function getPendingInviteContext() {
  return uni.getStorageSync(STORAGE_KEY) || null
}

export function clearPendingInviteContext() {
  uni.removeStorageSync(STORAGE_KEY)
}

export async function consumePendingInviteContext() {
  const context = getPendingInviteContext()
  if (!context || !context.inviteCode || !hasLogin()) return null
  try {
    await bindInviteCode({
      inviteCode: context.inviteCode,
      sourceChannel: context.sourceChannel,
      sourcePage: context.sourcePage
    }, { silent: true })
    clearPendingInviteContext()
    return true
  } catch (error) {
    const message = error && error.message ? error.message : ''
    if (/不能绑定自己|已经绑定其他推广员|推广员不存在|推广员已停用/.test(message)) {
      clearPendingInviteContext()
      uni.showToast({ title: message, icon: 'none' })
    }
    return false
  }
}
