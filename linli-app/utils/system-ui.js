let statusBarHeight = 0

export function getStatusBarHeight() {
  if (statusBarHeight) return statusBarHeight
  try {
    const info = uni.getWindowInfo ? uni.getWindowInfo() : uni.getSystemInfoSync()
    statusBarHeight = Number(info.statusBarHeight || 0)
  } catch (e) {
    statusBarHeight = 0
  }
  return statusBarHeight
}

export function getMenuButtonBottom() {
  try {
    const info = uni.getWindowInfo ? uni.getWindowInfo() : uni.getSystemInfoSync()
    const menu = uni.getMenuButtonBoundingClientRect && uni.getMenuButtonBoundingClientRect()
    if (menu && menu.bottom) return menu.bottom
    return getStatusBarHeight() + 44
  } catch (e) {
    return getStatusBarHeight() + 44
  }
}
