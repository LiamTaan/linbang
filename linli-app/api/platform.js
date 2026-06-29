import { get } from '@/utils/request'

export function getAppSettings() {
  return get('/platform-config/app-settings', {}, { auth: false })
}

export function getAgreement() {
  return get('/platform-config/agreement', {}, { auth: false })
}
