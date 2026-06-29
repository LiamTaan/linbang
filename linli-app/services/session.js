import { bootstrapSession } from '@/services/app-bootstrap'
import { clearPendingSocialAuth, clearSession, setTokenInfo } from '@/utils/auth'

export async function applyLoginSession(loginResp) {
  if (!loginResp || !loginResp.accessToken) {
    throw new Error('登录返回数据不完整')
  }
  setTokenInfo({
    accessToken: loginResp.accessToken,
    refreshToken: loginResp.refreshToken,
    expiresTime: loginResp.expiresTime,
    userId: loginResp.userId
  })
  clearPendingSocialAuth()
  const session = await bootstrapSession()
  uni.$emit('linbang:session-updated', session)
  return session
}

export function logoutSession() {
  clearSession()
  uni.$emit('linbang:session-cleared')
}

export function getDefaultAfterLoginPage() {
  return '/pages/index/index'
}

export function redirectAfterLogin(redirect) {
  const targetUrl = redirect && redirect !== '/pages/login/login'
    ? decodeURIComponent(redirect)
    : getDefaultAfterLoginPage()
  uni.reLaunch({
    url: targetUrl
  })
}
