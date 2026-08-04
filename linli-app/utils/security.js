const MAX_EXTERNAL_URL_LENGTH = 4096
const UNSAFE_URL_CHARACTERS = /[\u0000-\u001F\u007F\\]/

const SOCIAL_AUTHORIZE_HOSTS = {
  32: ['open.weixin.qq.com'],
  40: ['openauth.alipay.com', 'openauth.alipaydev.com']
}

function normalizeHost(host) {
  return String(host || '').toLowerCase().replace(/\.$/, '')
}

export function normalizeExternalHttpsUrl(rawUrl, allowedHosts = []) {
  if (typeof rawUrl !== 'string' || rawUrl.length > MAX_EXTERNAL_URL_LENGTH
    || UNSAFE_URL_CHARACTERS.test(rawUrl)) {
    return ''
  }
  const value = rawUrl.trim()
  if (!value) {
    return ''
  }
  try {
    const parsed = new URL(value)
    const hostname = normalizeHost(parsed.hostname)
    const normalizedAllowedHosts = allowedHosts.map(normalizeHost)
    if (parsed.protocol !== 'https:' || !hostname || parsed.username || parsed.password) {
      return ''
    }
    if (parsed.port && parsed.port !== '443') {
      return ''
    }
    if (normalizedAllowedHosts.length && !normalizedAllowedHosts.includes(hostname)) {
      return ''
    }
    return parsed.href
  } catch (error) {
    return ''
  }
}

export function normalizeSocialAuthorizeUrl(rawUrl, type) {
  const allowedHosts = SOCIAL_AUTHORIZE_HOSTS[Number(type)]
  if (!allowedHosts) {
    return ''
  }
  return normalizeExternalHttpsUrl(rawUrl, allowedHosts)
}

export function openExternalHttpsUrl(rawUrl, allowedHosts = []) {
  const url = normalizeExternalHttpsUrl(rawUrl, allowedHosts)
  if (!url) {
    return false
  }
  let opened = false
  // #ifdef APP-PLUS
  if (typeof plus !== 'undefined' && plus.runtime && plus.runtime.openURL) {
    plus.runtime.openURL(url)
    opened = true
  }
  // #endif
  // #ifdef H5
  if (typeof window !== 'undefined' && window.location) {
    window.location.assign(url)
    opened = true
  }
  // #endif
  return opened
}
