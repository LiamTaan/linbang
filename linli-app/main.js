import App from './App'
import { isPublicPage } from './config/app'
import { ensurePageAuth } from './utils/navigation'
import * as format from './utils/format'

function buildFullPath(route, options) {
  const query = Object.keys(options || {})
    .filter((key) => options[key] !== undefined && options[key] !== '')
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(options[key])}`)
    .join('&')
  return `/${route}${query ? `?${query}` : ''}`
}

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
Vue.mixin({
  onShow() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    if (!currentPage || !currentPage.route || isPublicPage(currentPage.route)) {
      return
    }
    ensurePageAuth(currentPage.route, buildFullPath(currentPage.route, currentPage.options))
  }
})
Vue.prototype.$fmt = format
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  app.mixin({
    onShow() {
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      if (!currentPage || !currentPage.route || isPublicPage(currentPage.route)) {
        return
      }
      ensurePageAuth(currentPage.route, buildFullPath(currentPage.route, currentPage.options))
    }
  })
  app.config.globalProperties.$fmt = format
  return {
    app
  }
}
// #endif
