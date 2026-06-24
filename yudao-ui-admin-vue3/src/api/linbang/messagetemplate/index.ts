import request from '@/config/axios'

export interface MessageTemplate {
  id?: number
  templateCode: string
  templateName: string
  templateType: string
  channelType: string
  content: string
  status: string
  createTime?: string
  updateTime?: string
}

export interface MessageTemplateDetail extends MessageTemplate {
  sendCount?: number
  successCount?: number
  failedCount?: number
  pendingCount?: number
  channelStats?: MessageTemplateChannelStat[]
  recentRecords?: MessageTemplateRecord[]
}

export interface MessageTemplateChannelStat {
  channelType?: string
  recordCount?: number
}

export interface MessageTemplateRecord {
  id: number
  receiverUserId?: number
  receiverUserNo?: string
  receiverUserNickname?: string
  receiverUserMobile?: string
  channelType?: string
  bizType?: string
  bizId?: number
  sendStatus?: string
  sendTime?: string
  failReason?: string
  createTime?: string
}

export const MessageTemplateApi = {
  getMessageTemplatePage: async (params: any) => {
    return await request.get({ url: '/message/template/page', params })
  },
  getMessageTemplate: async (id: number) => {
    return await request.get<MessageTemplateDetail>({ url: `/message/template/get?id=${id}` })
  },
  createMessageTemplate: async (data: MessageTemplate) => {
    return await request.post({ url: '/message/template/create', data })
  },
  updateMessageTemplate: async (data: MessageTemplate) => {
    return await request.put({ url: '/message/template/update', data })
  }
}
