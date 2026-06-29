import { APP_CONFIG, isPublicPage } from '@/config/app'
import { hasLogin } from '@/utils/auth'

let redirecting = false

export function navigateToLogin(redirectUrl) {
  if (redirecting) {
    return
  }
  redirecting = true
  const suffix = redirectUrl ? `?redirect=${encodeURIComponent(redirectUrl)}` : ''
  uni.reLaunch({
    url: `${APP_CONFIG.loginPage}${suffix}`,
    complete: () => {
      setTimeout(() => {
        redirecting = false
      }, 300)
    }
  })
}

export function ensurePageAuth(route, fullPath) {
  if (isPublicPage(route) || hasLogin()) {
    return true
  }
  navigateToLogin(fullPath || `/${route}`)
  return false
}
