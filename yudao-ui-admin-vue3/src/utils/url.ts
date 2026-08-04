const OPENABLE_PROTOCOLS = new Set(['http:', 'https:', 'blob:'])
const EMBEDDABLE_PROTOCOLS = new Set(['http:', 'https:'])
const NAVIGABLE_PROTOCOLS = new Set(['http:', 'https:'])
const MAX_URL_LENGTH = 8192
const UNSAFE_URL_CHARACTERS = /[\u0000-\u001F\u007F\\]/
const INVALID_PERCENT_ENCODING = /%(?![0-9A-Fa-f]{2})/
const ENCODED_UNSAFE_URL_CHARACTERS = /%(?:0[0-9A-F]|1[0-9A-F]|7F|5C)/i

function containsEncodedUnsafeCharacters(url: string): boolean {
  let encoded = url
  for (let index = 0; index < 8; index += 1) {
    if (INVALID_PERCENT_ENCODING.test(encoded) || ENCODED_UNSAFE_URL_CHARACTERS.test(encoded)) {
      return true
    }
    const decodedPercent = encoded.replace(/%25/gi, '%')
    if (decodedPercent === encoded) {
      return false
    }
    encoded = decodedPercent
  }
  return true
}

function hasAllowedProtocol(url: string, protocols: Set<string>): boolean {
  if (
    !url ||
    url.length > MAX_URL_LENGTH ||
    url !== url.trim() ||
    UNSAFE_URL_CHARACTERS.test(url) ||
    containsEncodedUnsafeCharacters(url)
  ) {
    return false
  }
  try {
    const parsed = new URL(url, window.location.origin)
    return protocols.has(parsed.protocol) && !parsed.username && !parsed.password
  } catch {
    return false
  }
}

/** 判断 URL 是否允许在新窗口打开 */
export function isOpenableUrl(url?: string | null): boolean {
  if (!url) {
    return false
  }
  return hasAllowedProtocol(url, OPENABLE_PROTOCOLS)
}

export function toOpenableUrl(url?: string | null): string | undefined {
  return url && isOpenableUrl(url) ? url : undefined
}

export function toEmbeddableUrl(url?: string | null): string | undefined {
  return url && hasAllowedProtocol(url, EMBEDDABLE_PROTOCOLS) ? url : undefined
}

export function toNavigableUrl(url?: string | null): string | undefined {
  return url && hasAllowedProtocol(url, NAVIGABLE_PROTOCOLS) ? url : undefined
}

export function toSafeInternalPath(path?: string | null): string | undefined {
  if (
    !path ||
    !path.startsWith('/') ||
    path.startsWith('//') ||
    !hasAllowedProtocol(path, NAVIGABLE_PROTOCOLS)
  ) {
    return undefined
  }
  try {
    return new URL(path, window.location.origin).origin === window.location.origin
      ? path
      : undefined
  } catch {
    return undefined
  }
}

/** 安全打开 URL */
export function openSafeUrl(url?: string | null): void {
  const safeUrl = toOpenableUrl(url)
  if (!safeUrl) {
    return
  }
  window.open(safeUrl, '_blank', 'noopener,noreferrer')
}

export function navigateToSafeUrl(url?: string | null): boolean {
  const safeUrl = toNavigableUrl(url)
  if (!safeUrl) {
    return false
  }
  window.location.assign(safeUrl)
  return true
}
