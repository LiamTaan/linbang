import { APP_CONFIG, isPublicPage } from '@/config/app'
import { hasLogin } from '@/utils/auth'

let redirecting = false

const DEFAULT_BACK_FALLBACK = '/pages/my/my'
const MAX_ROUTE_LENGTH = 2048
const UNSAFE_ROUTE_CHARACTERS = /[\u0000-\u001F\u007F\\#]/
const INVALID_PERCENT_ENCODING = /%(?![0-9A-Fa-f]{2})/
const ENCODED_UNSAFE_ROUTE_CHARACTERS = /%(?:0[0-9A-F]|1[0-9A-F]|7F|5C|23)/i

export const TAB_BAR_PAGES = [
  '/pages/index/index',
  '/pages/order/order',
  '/pages/news/news',
  '/pages/my/my'
]

const NAVIGABLE_PAGE_PATHS = new Set([
  ...TAB_BAR_PAGES,
  '/pages/detail_of_earnings/detail_of_earnings',
  '/pages/certificate/certificate',
  '/pages/certificate/realname_edit',
  '/pages/certificate/qualification_edit',
  '/pages/certificate/qualification_list',
  '/pages/set/set',
  '/pages/promotion_center/promotion_center',
  '/pages/regional_partner/regional_partner',
  '/pages/regional_partner/region_detail',
  '/pages/regional_partner/promote_stat',
  '/pages/regional_partner/entry_audit_list',
  '/pages/regional_partner/entry_audit_detail',
  '/pages/regional_partner/dispute_list',
  '/pages/regional_partner/dispute_detail',
  '/pages/regional_partner/price_report_list',
  '/pages/regional_partner/price_report_create',
  '/pages/regional_partner/price_report_detail',
  '/pages/regional_partner/instruction_list',
  '/pages/regional_partner/instruction_detail',
  '/pages/identity_application/identity_application',
  '/pages/merchant_entry/merchant_entry',
  '/pages/role_apply_detail/role_apply_detail',
  '/pages/role_apply_form/role_apply_form',
  '/pages/refund/refund',
  '/pages/my_wallet/my_wallet',
  '/pages/bank_card_management/bank_card_management',
  '/pages/complaint/complaint',
  '/pages/my_credit/my_credit',
  '/pages/feedback/feedback',
  '/pages/my_reward/my_reward',
  '/pages/address_management/address_management',
  '/pages/appeal/appeal',
  '/pages/order_receiving_status/order_receiving_status',
  '/pages/withdraw_deposit/withdraw_deposit',
  '/pages/evaluation_service/evaluation_service',
  '/pages/order_preview/order_preview',
  '/pages/order_detail/order_detail',
  '/pages/accept_order_detail/accept_order_detail',
  '/pages/split_order_details/split_order_details',
  '/pages/merchant_business/merchant_business'
])

const ROUTE_ALIAS_MAP = {
  '/pages/qualification/index': '/pages/certificate/certificate'
}

function decodeLeadingRoute(route) {
  let value = route
  for (let index = 0; index < 2 && !value.startsWith('/'); index += 1) {
    try {
      const decoded = decodeURIComponent(value)
      if (decoded === value) {
        break
      }
      value = decoded
    } catch (error) {
      return ''
    }
  }
  return value
}

function containsEncodedUnsafeCharacters(route) {
  let encoded = route
  for (let index = 0; index < 8; index += 1) {
    if (INVALID_PERCENT_ENCODING.test(encoded) || ENCODED_UNSAFE_ROUTE_CHARACTERS.test(encoded)) {
      return true
    }
    const decodedPercent = encoded.replace(/%25/ig, '%')
    if (decodedPercent === encoded) {
      return false
    }
    encoded = decodedPercent
  }
  return true
}

export function normalizeAppRoute(rawRoute) {
  if (typeof rawRoute !== 'string' || rawRoute.length > MAX_ROUTE_LENGTH
    || UNSAFE_ROUTE_CHARACTERS.test(rawRoute)) {
    return ''
  }
  let route = decodeLeadingRoute(rawRoute.trim())
  if (!route || route.length > MAX_ROUTE_LENGTH || UNSAFE_ROUTE_CHARACTERS.test(route)
    || containsEncodedUnsafeCharacters(route)) {
    return ''
  }
  const queryIndex = route.indexOf('?')
  const pagePath = queryIndex >= 0 ? route.slice(0, queryIndex) : route
  const query = queryIndex >= 0 ? route.slice(queryIndex) : ''
  const normalizedPagePath = ROUTE_ALIAS_MAP[pagePath] || pagePath
  if (!NAVIGABLE_PAGE_PATHS.has(normalizedPagePath)) {
    return ''
  }
  route = `${normalizedPagePath}${query}`
  return route
}

export function openAppRoute(rawRoute) {
  const route = normalizeAppRoute(rawRoute)
  if (!route) {
    return false
  }
  const pagePath = route.split('?')[0]
  if (TAB_BAR_PAGES.includes(pagePath)) {
    uni.switchTab({ url: pagePath })
    return true
  }
  uni.navigateTo({ url: route })
  return true
}

export function navigateToLogin(redirectUrl) {
  if (redirecting) {
    return
  }
  redirecting = true
  const safeRedirectUrl = normalizeAppRoute(redirectUrl)
  const suffix = safeRedirectUrl ? `?redirect=${encodeURIComponent(safeRedirectUrl)}` : ''
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
  const isTabPage = useSwitchTab && TAB_BAR_PAGES.includes(fallbackUrl)
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
