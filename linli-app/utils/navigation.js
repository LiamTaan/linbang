import { APP_CONFIG, isPublicPage } from '@/config/app'
import { hasLogin } from '@/utils/auth'

let redirecting = false

const DEFAULT_BACK_FALLBACK = '/pages/my/my'

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

export function navigateBack(options = {}) {
  const {
    delta = 1,
    fallbackUrl = DEFAULT_BACK_FALLBACK,
    useSwitchTab = true
  } = options
  const pages = getCurrentPages()
  if (Array.isArray(pages) && pages.length > delta) {
    uni.navigateBack({ delta })
    return
  }
  if (!fallbackUrl) {
    return
  }
  const isTabPage = useSwitchTab && ['/pages/index/index', '/pages/order/order', '/pages/news/news', '/pages/my/my'].includes(fallbackUrl)
  const action = isTabPage ? uni.switchTab : uni.reLaunch
  action({
    url: fallbackUrl,
    fail: () => {
      uni.reLaunch({
        url: fallbackUrl
      })
    }
  })
}
