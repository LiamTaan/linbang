import request from '@/config/axios'

export interface MessageScene {
  id?: number
  sceneCode: string
  sceneName: string
  messageCategory: string
  defaultChannels: string
  mandatorySms: boolean
  voiceEnabled: boolean
  status: string
  bizType?: string
  remark?: string
  createTime?: string
}

export const MessageSceneApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/message/scene/page', params })
  },
  get: async (id: number) => {
    return await request.get<MessageScene>({ url: `/message/scene/get?id=${id}` })
  },
  create: async (data: MessageScene) => {
    return await request.post({ url: '/message/scene/create', data })
  },
  update: async (data: MessageScene) => {
    return await request.put({ url: '/message/scene/update', data })
  },
  updateStatus: async (id: number, status: string) => {
    return await request.put({ url: `/message/scene/update-status?id=${id}&status=${status}` })
  }
}
