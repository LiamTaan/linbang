import type { App } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { createRouter, createWebHistory } from 'vue-router'
import remainingRouter from './modules/remaining'

const DYNAMIC_IMPORT_RETRY_KEY = '__route_dynamic_import_retry__'
const DYNAMIC_IMPORT_ERROR_PATTERNS = [
  'Failed to fetch dynamically imported module',
  'Importing a module script failed'
]

const isDynamicImportFailure = (message: string) =>
  DYNAMIC_IMPORT_ERROR_PATTERNS.some((pattern) => message.includes(pattern))

const buildRetryUrl = (fullPath: string) => {
  const url = new URL(fullPath, window.location.origin)
  url.searchParams.set('__route_reload__', `${Date.now()}`)
  return `${url.pathname}${url.search}${url.hash}`
}

export const getDynamicImportRetryCacheKey = (fullPath: string) =>
  `${DYNAMIC_IMPORT_RETRY_KEY}:${fullPath}`

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_BASE_PATH), // createWebHashHistory URL带#，createWebHistory URL不带#
  strict: true,
  routes: remainingRouter as RouteRecordRaw[],
  scrollBehavior: () => {
    // 新开标签时、返回标签时，滚动条回到顶部，否则会保留上次标签的滚动位置。
    const scrollbarWrap = document.querySelector('.v-layout-content-scrollbar .el-scrollbar__wrap')
    if (scrollbarWrap) {
      // scrollbarWrap.scrollTo({ left: 0, top: 0, behavior: 'auto' })
      scrollbarWrap.scrollTop = 0
    }
    return { left: 0, top: 0 }
  }
})

// 处理动态导入失败（如重新构建后 chunk 哈希变化），自动跳转到目标页面
router.onError((error, to) => {
  if (isDynamicImportFailure(error.message)) {
    const retryKey = getDynamicImportRetryCacheKey(to.fullPath)
    const hasRetried = sessionStorage.getItem(retryKey) === '1'
    if (hasRetried) {
      sessionStorage.removeItem(retryKey)
      console.error('[router] Dynamic import recovery failed after one retry:', to.fullPath, error)
      return
    }
    sessionStorage.setItem(retryKey, '1')
    window.location.replace(buildRetryUrl(to.fullPath))
  }
})

export const resetRouter = (): void => {
  const resetWhiteNameList = ['Redirect', 'RedirectRoot', 'Login', 'NoFound', 'Home']
  router.getRoutes().forEach((route) => {
    const { name } = route
    if (name && !resetWhiteNameList.includes(name as string)) {
      router.hasRoute(name) && router.removeRoute(name)
    }
  })
}

export const setupRouter = (app: App<Element>) => {
  app.use(router)
}

export default router
