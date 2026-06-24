import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { buildDynamicKeyHeaders } from '@/api/linbang/security'

export interface PlatformConfig {
  id?: number
  category: string
  name: string
  key: string
  value: string
  visible: boolean
  remark?: string
  createTime?: string | Dayjs
}

export interface PlatformConfigDetail extends PlatformConfig {
  updateTime?: string | Dayjs
  summary?: {
    sameCategoryCount?: number
    visibleCount?: number
    hiddenCount?: number
    agreementCategory?: boolean
    platformCategory?: boolean
  }
  relatedConfigs?: Array<{
    id?: number
    name?: string
    key?: string
    visible?: boolean
    remark?: string
    createTime?: string | Dayjs
  }>
}

export const PlatformConfigApi = {
  getPlatformConfigPage: async (params: any) => {
    return await request.get({ url: '/platform-config/setting/page', params })
  },

  getPlatformConfig: async (id: number) => {
    return await request.get<PlatformConfigDetail>({ url: `/platform-config/setting/get?id=${id}` })
  },

  createPlatformConfig: async (data: PlatformConfig, verifyToken?: string) => {
    return await request.post({
      url: '/platform-config/setting/create',
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  updatePlatformConfig: async (data: PlatformConfig, verifyToken?: string) => {
    return await request.put({
      url: '/platform-config/setting/update',
      data,
      headers: buildDynamicKeyHeaders(verifyToken)
    })
  },

  exportPlatformConfig: async (params: any) => {
    return await request.download({ url: '/platform-config/setting/export-excel', params })
  }
}
